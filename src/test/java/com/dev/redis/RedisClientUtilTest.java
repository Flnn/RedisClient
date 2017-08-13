package com.dev.redis;

public class RedisClientUtilTest {

    public static void main(String[] args) {
        String key = "product_stock";
        String value = "100";
        RedisClientUtil.stringSet(key,value);
    }
}
