package com.dev.redis;

public class RedisClientUtilTest {

    public static void main(String[] args) {
        String key = "product_stock";
        String value = "100";
        try {
            RedisClientUtil.stringSet("stock","product_10101","1001");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
