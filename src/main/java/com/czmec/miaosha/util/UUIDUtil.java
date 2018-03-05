package com.czmec.miaosha.util;

import java.util.UUID;

/**
 * Created by user on 2018/3/5.
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
