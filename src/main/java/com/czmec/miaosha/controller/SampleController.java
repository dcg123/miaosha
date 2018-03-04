package com.czmec.miaosha.controller;

import com.czmec.miaosha.domain.User;
import com.czmec.miaosha.redis.RedisService;
import com.czmec.miaosha.redis.UserKey;
import com.czmec.miaosha.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by user on 2018/3/4.
 */
@Controller
@RequestMapping("/demo")
public class SampleController {


    @Autowired
    RedisService redisService;
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user=redisService.get(UserKey.getByName,""+1,User.class);
        System.out.print(user);
        return Result.success(user);
    }

    @RequestMapping("/redis/Set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user=new User();
        user.setPassword("1");
        user.setUsername("1");
        boolean v1=redisService.set(UserKey.getByName,""+1,user);
        return Result.success(true);

    }

}
