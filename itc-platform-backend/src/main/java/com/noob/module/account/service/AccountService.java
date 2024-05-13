package com.noob.module.account.service;

import com.noob.module.admin.base.user.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName
 * @Description 账号管理相关
 * @Author Huh-x
 * @Date 2024 2024/4/30 14:19
 */
public interface AccountService {
    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword,HttpServletRequest request);

    /**
     * 用户注销
     *
     * @return
     */
    void userLogout();

    /**
     * 发送邮箱验证码
     * @return
     */
    boolean sendEmailCode(String email);

    /**
     * 绑定邮箱
     * @return
     */
    boolean bindEmail(String email,String code);

}
