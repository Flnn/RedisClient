package com.dev.redis;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
     * @return redis返回值
     */
    public static String stringSet(String cacheType, String key, String value) throws Exception {
        CommonResult keyResult = getGeneralKey(cacheType, key);
        if(!keyResult.isSuccess()){
            throw new Exception(keyResult.getMsg());
        }
        String result = clusterClient.set(keyResult.getMsg(), value);
        logger.info("设置缓存,key ==> {}, value==> {},redis返回值：{} ",keyResult.getMsg(),value,result);
        return result;
    }

    /**
     * 批量设置 使用Map封装多组数据，对应redis mset方法
     * @param cacheType 缓存类型
     * @param dataMap   数据集 key-value形式
     */
    public static String StringMapSet(String cacheType, Map<String,String> dataMap) throws Exception {
        if(dataMap == null || dataMap.size()==0){
            throw new Exception("ERROR!! 数据集不能为空");
        }
        String[] strArray = new String[dataMap.size() * 2];
        int count=0;
        for(String key : dataMap.keySet()){
            CommonResult keyResult = getGeneralKey(cacheType, key);
            if(!keyResult.isSuccess()){
                throw new Exception(keyResult.getMsg());
            }
            strArray[count] = keyResult.getMsg();
            strArray[count++] = dataMap.get(key);
        }
        String result = clusterClient.mset(strArray);
        return result;
    }

    /**
     * 从缓存中取值
     * @param cacheType
     * @param key
     * @return
     * @throws Exception
     */
    public static String stringGet(String cacheType, String key) throws Exception {
        CommonResult keyResult = getGeneralKey(cacheType, key);
        if(!keyResult.isSuccess()){
            throw new Exception(keyResult.getMsg());
        }
        String getResult = clusterClient.get(keyResult.getMsg());
        return getResult;
    }

    /**
     * 从redis中获取json字符串，转换成指定对象返回
     * @param cacheType
     * @param key
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object objectGetByJsonString(String cacheType, String key,Class clazz) throws Exception {
        CommonResult keyResult = getGeneralKey(cacheType, key);
        if(!keyResult.isSuccess()){
            throw new Exception(keyResult.getMsg());
        }
        String jsonString = clusterClient.get(keyResult.toString());
        Gson gson = new Gson();
        return gson.fromJson(jsonString, clazz);
    }

    /**
     * 将一个对象转成JSON字符串，使用String类型存储到redis
     * @param cacheType
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static String objectSetByJsonString(String cacheType, String key, Object data) throws Exception{
        CommonResult keyResult = getGeneralKey(cacheType, key);
        if(!keyResult.isSuccess()){
            throw new Exception(keyResult.getMsg());
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(data);
        return clusterClient.set(keyResult.getMsg(), jsonStr);
    }

    /**
     * 存储某一类对象组成的集合
     * @param cacheType
     * @param key
     * @param listObject
     * @param <E>
     * @return
     * @throws Exception
     */
    public static <E> String listObjectSet(String cacheType, String key, List<E> listObject) throws Exception {
        CommonResult keyResult = getGeneralKey(cacheType, key);
        if(!keyResult.isSuccess()){
            throw new Exception(keyResult.getMsg());
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(listObject);
        String result = clusterClient.set(keyResult.getMsg(), jsonStr);
        return result;
    }

    /**
     * 从缓存中获取一个字符串，转换成List<Object>
     * @param cacheType
     * @param key
     * @param type 示例用法 new TypeToken<List<User>>(){}.getType(); TypeToken 取自Gson工具包
     * @param <E>
     * @return
     */
    public static <E> List<E> listObjectGet(String cacheType, String key, Type type) throws Exception {
        CommonResult keyResult = getGeneralKey(cacheType, key);
        if(!keyResult.isSuccess()){
            throw new Exception(keyResult.getMsg());
        }
        String jsonStr = clusterClient.get(keyResult.getMsg());
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, type);
    }

    /**
     * 将一个对象以hash类型存储到redis中
     * @param cacheType
     * @param key
     * @param object
     * @param <E>
     * @return
     */
    public static <E> String hashSet(String cacheType, String key,E object) {

        return null;
    }

    /**
     * 批量缓存对象，使用hash类型
     * @param cacheType
     * @param key
     * @param data
     * @param <E>
     * @return
     */
    public static <E> String hashMultiSet(String cacheType, String key, List<E> data) {

        return null;
    }

    /**
     * 检验参数并返回拼装好的key
     * @param cacheType
     * @param key
     * @return
     */
    private static CommonResult getGeneralKey(String cacheType, String key) {

        if(StringUtils.isBlank(cacheType)){
            return CommonResult.createByErrorMessage("cache_type不合法!!!" + cacheType);
        }
        if(StringUtils.isBlank(key)){
            return CommonResult.createByErrorMessage("key不合法!!!" + key);
        }
        return CommonResult.createBySuccessMessage(APP_NAME + "_" + cacheType + "_" + key);
    }
}
