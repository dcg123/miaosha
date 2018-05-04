package com.czmec.miaosha.controller;

import com.czmec.miaosha.access.AccessLimit;
import com.czmec.miaosha.domain.MiaoshaOrder;
import com.czmec.miaosha.domain.MiaoshaUser;
import com.czmec.miaosha.domain.OrderInfo;
import com.czmec.miaosha.rabbitmq.MQSender;
import com.czmec.miaosha.rabbitmq.MiaoshaMessage;
import com.czmec.miaosha.redis.GoodsKey;
import com.czmec.miaosha.redis.RedisService;
import com.czmec.miaosha.result.CodeMsg;
import com.czmec.miaosha.result.Result;
import com.czmec.miaosha.service.GoodsService;
import com.czmec.miaosha.service.MiaoshaService;
import com.czmec.miaosha.service.MiaoshaUserService;
import com.czmec.miaosha.service.OrderService;
import com.czmec.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

	@Autowired
    MiaoshaUserService userService;
	
	@Autowired
    RedisService redisService;
	
	@Autowired
    GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
    MiaoshaService miaoshaService;

	@Autowired
	MQSender mqSender;

	private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();
	//系统初始化 预加载数量
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsVoList=goodsService.listGoodsVo();
		if (goodsVoList==null){
			return;
		}
		for(GoodsVo goods:goodsVoList){
			redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}

	/**
	 * QPS:1306
	 * 5000 * 10
	 * */
	/**
	 *  GET POST有什么区别？
	 * */
    @RequestMapping(value="/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                     @RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
		//内存标记，减少redis访问
		boolean over = localOverMap.get(goodsId);
		if(over) {
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
    	//预减库存
		Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
    	if (stock<0){
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//入队
		MiaoshaMessage mm=new MiaoshaMessage();
		mm.setUser(user);
		mm.setGoodsid(goodsId);
		mqSender.sendMiaoshaMessage(mm);
		return Result.success(0);//排队中
//    	//判断库存
//    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
//    	int stock = goods.getStockCount();
//    	if(stock <= 0) {
//    		return Result.error(CodeMsg.MIAO_SHA_OVER);
//    	}
//    	//判断是否已经秒杀到了
//    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//    	if(order != null) {
//    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
//    	}
//    	//减库存 下订单 写入秒杀订单
//    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//        return Result.success(orderInfo);
    }

	/**
	 * orderId：成功
	 * -1：秒杀失败
	 * 0： 排队中
	 * */
	@RequestMapping(value="/result", method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
									  @RequestParam("goodsId")long goodsId) {
		model.addAttribute("user", user);
		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
		return Result.success(result);
	}

	/**
	 * 获取path 隐藏秒杀接口
	 * 自定义注解 限制在规定时间内 请求次数
	 * seconds 代表秒数
	 * maxCount 代表最大请求数
	 * needLogin 是否需要登录
	 */

	@AccessLimit(seconds=5, maxCount=5, needLogin=true)
	@RequestMapping(value = "/path",method = RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoShaPath(HttpServletRequest request,MiaoshaUser user,@RequestParam("goodsId") long goodsId,
										 @RequestParam(value="verifyCode", defaultValue="0") int verifyCode){
		if (user==null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		//查看验证码是否正确
		boolean check=miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
		if (!check){
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		//获取path 一个user对应一个path  用于请求秒杀接口时验证
		String path=miaoshaService.createMiaoShaPath(user,goodsId);
		return Result.success(path);
	}

	/**
	 * 获取验证码
	 */
	@RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaVerifyCode(HttpServletRequest request, MiaoshaUser user, @RequestParam("goodsId") long goodsId,
											   HttpServletResponse response){
		if (user==null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		try {
			BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
			OutputStream out = response.getOutputStream();
			ImageIO.write(image, "JPEG", out);
			out.flush();
			out.close();
			return null;
		}catch (Exception e){
			e.printStackTrace();
			return Result.error(CodeMsg.MIAOSHA_FAIL);
		}
	}

}
