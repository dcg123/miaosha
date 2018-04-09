package com.czmec.miaosha.redis;

/**
 * @author dcg
 * Created by user on 2018/4/9.
 */
public class GoodsKey  extends BasePrefix{
    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
}
