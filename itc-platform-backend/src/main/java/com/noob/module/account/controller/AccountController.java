package com.noob.module.account.controller;

import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.account.service.AccountService;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.module.admin.base.user.model.dto.UserLoginRequest;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.model.vo.UserVO;
import com.noob.module.admin.base.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description 账号相关控制器
 * @Author Huh-x
 * @Date 2024 2024/4/30 14:14
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @Resource
    private UserService userService;

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
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout() {
        // 调用登陆退出方法
        accountService.userLogout();
        return ResultUtils.success(true);
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getCurrentLoginUser() {
        // 获取当前登陆用户信息（基于shiro获取）
        Subject subject = SecurityUtils.getSubject();
        LoginUserVO currentUser = (LoginUserVO) subject.getPrincipal();
        return ResultUtils.success(currentUser);
    }

    /**
     * 获取用户信息详情（获取当前登录用户）
     * @return
     */
    @GetMapping("/account/getUserVOMoreByCurrentLoginUser")
    public BaseResponse<UserVO> getUserVOMoreByCurrentLoginUser() {
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        // 获取用户信息详情
        return ResultUtils.success(userService.getUserVOMore(currentUser.getId()));
    }

}
