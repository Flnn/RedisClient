package com.dev.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * 对jedis进一步的封装
 * @author jffeng1
 * APP_NAME 应用名称
 * CACHE_TYPE 缓存类型
 * key值的设置规则 APP_NAME + CACHE_TYPE + KEY
 */
public class RedisClientUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisClientUtil.class);

    private static Set<HostAndPort> clusterAddressList = new HashSet<HostAndPort>();

    private static String APP_NAME = "tmnew";

    private static boolean status = false;

    static {
        clusterAddressList.add(new HostAndPort("192.168.1.141",6379));
        clusterAddressList.add(new HostAndPort("192.168.1.142",6379));
        clusterAddressList.add(new HostAndPort("192.168.1.143",6379));
        clusterAddressList.add(new HostAndPort("192.168.1.144",6379));
        clusterAddressList.add(new HostAndPort("192.168.1.145",6379));
        clusterAddressList.add(new HostAndPort("192.168.1.146",6379));
        JedisPoolConfig jedisConfig = new JedisPoolConfig();
        jedisConfig.setMaxTotal(20);
        jedisConfig.setMaxIdle(10);
        jedisConfig.setMaxWaitMillis(5000);
        clusterClient = new JedisCluster(clusterAddressList, jedisConfig);
        status = true;
    }

    private static JedisCluster clusterClient;

    public JedisCluster getClusterClient() {
        return clusterClient;
    }

    /**
     * 向redis写入普通String类型数值
     *
     * @param key
     * @param value
     */
    public static void stringSet(String cacheType, String key, String value) throws Exception {
        CommonResult keyResult = getGeneralKey(cacheType, key);
        if(!keyResult.isSuccess()){
            throw new Exception(keyResult.getMsg());
        }
        clusterClient.set(keyResult.getMsg(), value);
        logger.info("设置成功,key ==> " + keyResult.getMsg());
    }

    /**
     * 检验参数并返回拼装好的key
     * @param cacheType
     * @param key
     * @return
     */
    private static CommonResult getGeneralKey(String cacheType, String key) {

        if(StringUtils.isBlank(cacheType)){
            return CommonResult.createByErrorMessage("cache_type不合法!!!");
        }
        if(StringUtils.isBlank(key)){
            return CommonResult.createByErrorMessage("cache_type不合法!!!");
        }
        return CommonResult.createBySuccessMessage(APP_NAME + "_" + cacheType + "_" + key);
    }
}
