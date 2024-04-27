package com.noob.module.base.service;

import javax.annotation.Resource;

import com.noob.module.base.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 *
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userName = "noob";
        String userAccount = "noob";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            long result = userService.userRegister(userName,userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "noob";
            result = userService.userRegister(userName,userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }
}
