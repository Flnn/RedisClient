package com.dev.redis.distributedLock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;

/**
 * redis分布式锁的实现
 */
public class DistributedLock {

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    private static JedisPoolConfig jedisPoolConfig;

    private static JedisPool jedisPool;

    static {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig, "192.168.1.153",6379,3000,null);
    }

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    /**
     * 尝试获取分布式锁
     * @param lockKey key
     * @param requestId 请求线程标识
     * @param expireTime 过期时间
     * @return
     */
    public static boolean getDistributedLock(Jedis jedis,String lockKey, String requestId, int expireTime){
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST,SET_WITH_EXPIRE_TIME,expireTime);
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 分布式锁解锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }
}
