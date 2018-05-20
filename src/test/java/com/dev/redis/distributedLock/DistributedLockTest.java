package com.dev.redis.distributedLock;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class DistributedLockTest {

    private static final String KEY = "distribute-key";
    //模拟库存
    private AtomicLong stock = new AtomicLong(100);

    @Test
    public void testDistributedLock() {

        Thread t1 = new Thread(() -> {
            for(int i=0;i<100;i++){
                Jedis jedis = DistributedLock.getJedis();
                boolean result = DistributedLock.getDistributedLock(jedis,KEY,Thread.currentThread().getName(),1000);
                jedis.close();
                if(result){
                    if (stock.longValue() > 0) {
                        stock.decrementAndGet();
                        System.out.println("t1 get lock, make stock - 1 ,loop: " + i);
                    }
                    jedis = DistributedLock.getJedis();
                    DistributedLock.releaseDistributedLock(jedis, KEY, Thread.currentThread().getName());
                    jedis.close();
                }else{
                    System.out.println("t1 can not get lock, skip");
                    continue;
                }
            }
        },"t1");
        Thread t2 = new Thread(()-> {
            for (int j = 0; j < 100; j++) {
                Jedis jedis = DistributedLock.getJedis();
                boolean lockResult = DistributedLock.getDistributedLock(jedis, KEY, Thread.currentThread().getName(), 1000);
                jedis.close();
                if (lockResult) {
                    if (stock.longValue() > 0) {
                        stock.decrementAndGet();
                        System.out.println("t2 get lock , make stock - 1 ,loop: " + j);
                    }
                    jedis = DistributedLock.getJedis();
                    DistributedLock.releaseDistributedLock(jedis, KEY, Thread.currentThread().getName());
                    jedis.close();
                }else{
                    System.out.println("t2 can not get lock, skip");
                }
            }
        }, "t2");
        t1.start();
        t2.start();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}