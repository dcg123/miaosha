package com.czmec.miaosha.redis;

import com.czmec.miaosha.domain.User;

/**
 * Created by user on 2018/3/4.
 */
public class UserKey extends BasePrefix {
    private UserKey(String prefix){
        super(prefix);
    }
    public static UserKey getById=new UserKey("id");
    public static UserKey getByName=new UserKey("name");
}
