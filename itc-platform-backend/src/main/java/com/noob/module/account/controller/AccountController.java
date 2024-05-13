package com.noob.module.account.controller;

import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.account.service.AccountService;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.module.admin.base.user.model.dto.UserLoginRequest;
import com.noob.module.admin.base.user.model.entity.UserSign;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.model.vo.UserSignVO;
import com.noob.module.admin.base.user.model.vo.UserVO;
import com.noob.module.admin.base.user.service.UserExtendService;
import com.noob.module.admin.base.user.service.UserService;
import com.noob.module.admin.base.user.service.UserSignService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

    @Resource
    private UserSignService userSignService;

    @Resource
    private UserExtendService userExtendService;

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
    @GetMapping("/getUserVOMoreByCurrentLoginUser")
    public BaseResponse<UserVO> getUserVOMoreByCurrentLoginUser() {
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        // 获取用户信息详情
        return ResultUtils.success(userService.getUserVOMore(currentUser.getId()));
    }


    /**
     * 用户签到
     * @return
     */
    @PostMapping("/userSignIn")
    public BaseResponse<Long> userSignIn() {

        // 校验当前用户当天是否已签到（禁止重复签到）
        UserSignVO userSignVO = userSignService.validCurrentUserSign();
        ThrowUtils.throwIf(userSignVO != null, ErrorCode.OPERATION_ERROR,"用户当日已签到，请勿重复操作");

        UserSign userSign = new UserSign();
        userSign.setUid(ShiroUtil.getCurrentUserId());
        userSign.setTitle("签到");
        userSign.setSignInChannel("web");
        userSign.setSignInTime(new Date());
        // 默认签到默认添加10积分
        userSign.setScore(10);

        // 新增签到记录
        boolean result = userSignService.save(userSign);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"签到记录新增失败");

        // 修改用户积分
        boolean updateUserScoreRes = userExtendService.updateUserScore(ShiroUtil.getCurrentUserId(),10,UserConstant.USER_SCORE_OPER_TYPE_ADD);
        ThrowUtils.throwIf(!updateUserScoreRes, ErrorCode.OPERATION_ERROR,"用户积分更新失败");

        // 返回签到记录ID
        return ResultUtils.success(userSign.getId());
    }


    /**
     * 发送邮箱验证码
     * @return
     */
    @GetMapping("/sendEmailCode")
    public BaseResponse<Boolean> sendEmailCode(@RequestParam String email) {
        // 调用验证码服务获取邮箱验证码信息
        accountService.sendEmailCode(email);
        return ResultUtils.success(true);
    }

}
