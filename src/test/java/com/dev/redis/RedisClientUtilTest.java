package com.dev.redis;

public class RedisClientUtilTest {

    public static void main(String[] args) {
        CacheEntity obj = new CacheEntity();
        obj.setId(1001L);
        obj.setDataName("first_cache");
        obj.setDataType("cacheType");
        try {
            RedisClientUtil.hashSet("cache",String.valueOf(1001L),obj,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
