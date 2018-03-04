package com.czmec.miaosha.controller;

import com.czmec.miaosha.result.Result;
import com.czmec.miaosha.service.MiaoshaUserService;
import com.czmec.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by user on 2018/3/4.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);
    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value = "do_login",method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
        log.info(loginVo.toString());
        miaoshaUserService.login(response, loginVo);
        return Result.success(true);

    }

}
