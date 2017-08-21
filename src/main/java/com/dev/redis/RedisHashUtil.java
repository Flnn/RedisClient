package com.dev.redis;

import redis.clients.jedis.JedisCluster;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 期望此工具类可以实现Java POJO的解析
 */
public class RedisHashUtil {

    /**
     * 使用反射获取对象的所有属性和值
     * @param pojo
     * @param notIncludeEmpty 是否包含空值 null,"" "  " 等等
     * @return
     */
    public static Map<String,String> objectTransToMap(Object pojo,boolean notIncludeEmpty) throws IllegalAccessException {
        Map<String, String> result = new HashMap<>();
        Field[] fields = pojo.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Type fieldType = field.getGenericType();
            Object value = field.get(pojo);
            if(notIncludeEmpty && isEmpty(value)){
                continue;
            }
            if(fieldType.equals(Date.class)){
                //日期类型
                Date dateValue = (Date) value;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                result.put(field.getName(), sdf.format(dateValue));
            }else {
                result.put(field.getName(), String.valueOf(field.get(pojo)));
            }
        }
        return result;
    }

    /**
     * 封装对象
     * @param pojo
     * @param cluster
     * @param key
     * @return
     */
    public static void transToObject(Object pojo, JedisCluster cluster,String key){
        Field[] fields = pojo.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value = cluster.hget(key, field.getName());
            Type fieldType = field.getGenericType();
            if (fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
                //设置整型值
                String hgetIntegerValue = cluster.hget(key, field.getName());
                if(hgetIntegerValue != null) {
                    try {
                        field.set(pojo, Integer.valueOf(hgetIntegerValue));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else if (fieldType.equals(Long.class) || fieldType.equals(Long.TYPE)) {
                // 设置long型
                String hgetLongValue = cluster.hget(key, field.getName());
                if (hgetLongValue != null){
                    try {
                        field.set(pojo,Long.valueOf(hgetLongValue));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else if (fieldType.equals(Date.class)) {
                String hgetDateValue = cluster.hget(key, field.getName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (hgetDateValue != null) {
                    try {
                        field.set(pojo,sdf.parse(hgetDateValue));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }else if(fieldType.equals(BigDecimal.class)){
                String hgetBigDecimalValue = cluster.hget(key, field.getName());
                if (hgetBigDecimalValue != null) {
                    try {
                        field.set(pojo,new BigDecimal(hgetBigDecimalValue));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                String hgetStringValue = cluster.hget(key, field.getName());
                if (hgetStringValue != null) {
                    try {
                        field.set(pojo, hgetStringValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static boolean isEmpty(Object obj){
        if(obj ==null){
            return true;
        }else{
            if(obj.toString().trim().isEmpty()){
                return true;
            }else{
                return false;
            }
        }
    }

}
