package com.czmec.miaosha.redis;

/**
 * Created by user on 2018/3/4.
 */
public interface KeyPrefix {
    public int expireSeconds();
    public String getPrefix();
}
