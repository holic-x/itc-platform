package com.noob.module.admin.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.*;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.user.model.entity.User;
import com.noob.module.admin.user.model.vo.LoginUserVO;
import com.noob.module.admin.user.model.vo.UserVO;
import com.noob.framework.config.WxOpenConfig;
import com.noob.framework.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.module.admin.user.service.UserService;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.noob.module.admin.user.model.dto.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.noob.module.admin.user.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 * 用户信息管理：提供用户注册、登录注销、获取登录用户信息、后台管理用户、用户数据更新等（结合实际业务进行扩展）
 */
@Api(value = "用户管理", tags = "admin-用户管理")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userName = userRegisterRequest.getUserName();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userName,userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户登录（微信开放平台）
     */
    @GetMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 设置默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);

        // 设置用户默认状态为启用
        user.setUserStatus(UserConstant.USER_STATUS_ACTIVE);

        // 保存用户信息
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验用户操作更新对象是否为本人账号（一定程度上限制其更新操作，额外提供接口更新自己的信息）
        User loginUser = userService.getLoginUser(request);
        if(loginUser.getId().equals(deleteRequest.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"不允许用户更改自己的信息");
        }

        // 指定删除操作
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验用户操作更新对象是否为本人账号（一定程度上限制其更新操作，额外提供接口更新自己的信息）
        User loginUser = userService.getLoginUser(request);
        if(loginUser.getId().equals(userUpdateRequest.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"不允许用户更改自己的信息");
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        user.setUpdateTime(new Date());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> getUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
            HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }



    /**
     * 更新用户状态信息
     *
     * @param userStatusUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/handleUserStatus")
    public BaseResponse<Boolean> handleUserStatus(@RequestBody UserStatusUpdateRequest userStatusUpdateRequest,
                                              HttpServletRequest request) {
        if (userStatusUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验用户操作更新对象是否为本人账号（一定程度上限制其更新操作，额外提供接口更新自己的信息）
        User loginUser = userService.getLoginUser(request);
        if(loginUser.getId().equals(userStatusUpdateRequest.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"不允许用户更改自己的信息");
        }

        Long userId = userStatusUpdateRequest.getId();
        User findUser = userService.getById(userId);
        // 校验用户信息
        if(findUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 根据指定操作更新用户信息（此处根据操作类型更新用户状态信息）
        User user = new User();
        user.setId(userId);
        user.setUpdateTime(new Date());
        String operType = userStatusUpdateRequest.getOperType();
        Integer currentUserStatus = findUser.getUserStatus();
        if("active".equals(operType)){
            // 校验当前状态，避免重复激活
            if(currentUserStatus==UserConstant.USER_STATUS_ACTIVE){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前用户已激活，请勿重复操作");
            }
            user.setUserStatus(UserConstant.USER_STATUS_ACTIVE);
            userService.updateById(user);
        }else if("forbid".equals(operType)){
            // 校验当前状态，避免重复禁用
            if(currentUserStatus==UserConstant.USER_STATUS_FORBID){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前用户已禁用，请勿重复操作");
            }
            user.setUserStatus(UserConstant.USER_STATUS_FORBID);
            userService.updateById(user);
        }else {
            // 其余操作类型则不允许操作
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"指定操作类型错误，请联系管理员处理");
        }
        return ResultUtils.success(true);
    }




    // --------------- 批量操作定义 ---------------
    /**
     * 批量删除用户
     *
     * @param batchDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/batchDeleteUser")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteUser(@RequestBody BatchDeleteRequest batchDeleteRequest, HttpServletRequest request) {
        if (batchDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验用户操作更新对象是否为本人账号（一定程度上限制其更新操作，额外提供接口更新自己的信息）
        User loginUser = userService.getLoginUser(request);
        if(batchDeleteRequest.getIdList().contains(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"不允许用户更改自己的信息");
        }

        // 执行批量操作
        List<Long> idList = batchDeleteRequest.getIdList();
        if(idList == null || idList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"指定操作列表不能为空");
        }
        // 批量删除
        boolean b = userService.removeBatchByIds(idList);
        return ResultUtils.success(b);
    }
}
