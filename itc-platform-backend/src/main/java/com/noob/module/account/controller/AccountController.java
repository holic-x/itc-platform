package com.noob.module.account.controller;

import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.module.account.service.AccountService;
import com.noob.module.admin.base.user.model.dto.UserLoginRequest;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName Huh-x
 * @Description 账号相关控制器
 * @Author Huh-x
 * @Date 2024 2024/4/30 14:14
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用登陆验证方法（经Shiro机制处理）
        LoginUserVO loginUserVO = accountService.userLogin(userAccount, userPassword, request);

        // 返回登陆成功的用户信息
        return ResultUtils.success(loginUserVO);
    }


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // ShiroUtil.deleteCache();

        // 退出登陆
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            // 销毁SESSION(清理权限缓存)
            subject.logout();
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getCurrentLoginUser(HttpServletRequest request) {
        // 获取当前登陆用户信息（基于shiro获取）
        Subject subject = SecurityUtils.getSubject();
        LoginUserVO currentUser = (LoginUserVO)subject.getPrincipal();
        return ResultUtils.success(currentUser);
    }

}
