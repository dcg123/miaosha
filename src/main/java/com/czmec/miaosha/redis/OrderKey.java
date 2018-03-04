package com.czmec.miaosha.redis;

/**
 * Created by user on 2018/3/4.
 */
public class OrderKey extends BasePrefix{
    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
