package com.czmec.miaosha.service;

import com.czmec.miaosha.dao.MiaoshaUserDao;
import com.czmec.miaosha.domain.MiaoshaUser;
import com.czmec.miaosha.exception.GlobalException;
import com.czmec.miaosha.redis.MiaoShaUserKey;
import com.czmec.miaosha.redis.RedisService;
import com.czmec.miaosha.result.CodeMsg;
import com.czmec.miaosha.util.MD5Util;
import com.czmec.miaosha.util.UUIDUtil;
import com.czmec.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by user on 2018/3/4.
 */
@Service
public class MiaoshaUserService {

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    public static final String COOKI_NAME_TOKEN = "token";
    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //保存用户信息到缓存中
        //生成cookie
        String token= UUIDUtil.uuid();
        addCokie(response,token,user);
        return true;
    }

    private void addCokie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoShaUserKey.token,token,user);
        Cookie cookie=new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    public MiaoshaUser getByToken(HttpServletResponse response,String token){
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (user==null){
            addCokie(response,token,user);
        }
        return user;
    }
}
