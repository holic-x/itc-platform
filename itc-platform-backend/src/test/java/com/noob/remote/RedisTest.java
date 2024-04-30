package com.noob.remote;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

/**
 * @ClassName Huh-x
 * @Description TODO
 * @Author Huh-x
 * @Date 2024 2024/4/30 8:51
 */
@SpringBootTest
public class RedisTest {


    /**
     * 测试远程redis连接
     */
    @Test
    void testConnect(){
        Jedis jedis = new Jedis("",6379, 60000);
        jedis.auth("123456");
        jedis.set("name", "Tom");
        jedis.set("age", "18");
        System.out.println(jedis.get("name") + jedis.get("age"));
    }
}
