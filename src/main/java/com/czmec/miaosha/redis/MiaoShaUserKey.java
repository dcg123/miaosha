package com.czmec.miaosha.redis;

/**
 * Created by user on 2018/3/4.
 */
public class MiaoShaUserKey extends BasePrefix {
    public static final int TOKEN_EXPIRE = 3600*24 * 2;
    private MiaoShaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE, "tk");
}
