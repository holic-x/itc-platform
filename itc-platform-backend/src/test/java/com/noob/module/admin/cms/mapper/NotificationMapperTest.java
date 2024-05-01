package com.noob.module.admin.cms.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ClassName NotificationMapperTest
 * @Description TODO
 * @Author holic-x
 * @Date 2024/5/1 14:56
 */
@SpringBootTest
class NotificationMapperTest {

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    void test(){
        System.out.println(notificationMapper.getVOById(1785559907269885954l));
    }

}