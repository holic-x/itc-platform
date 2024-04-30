package com.noob.module.admin.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.*;
import com.noob.framework.constant.CommonConstant;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.admin.api.model.dto.*;
import com.noob.module.admin.api.model.enums.InterfaceInfoEnum;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.api.model.entity.InterfaceInfo;
import com.noob.module.admin.api.model.entity.UserInterfaceInfo;
import com.noob.module.admin.api.service.InterfaceInfoService;
import com.noob.module.admin.api.service.UserInterfaceInfoService;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * 接口信息
 */
@RestController("admin-InterfaceInfoController")
@RequestMapping("/admin/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;


    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

//    @Resource
//    private ApiClient apiClient;

//    @Value("${custom.gateway-ip}")
//    private String GATEWAY_IP;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest) {
        // 调用方法获取新增接口信息
        long newInterfaceInfoId = interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest);
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(currentUser.getId()) && !ShiroUtil.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(currentUser.getId()) && !ShiroUtil.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/")
    public BaseResponse<InterfaceInfo> getInterfaceInfoVOById(long id ) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验请求参数，筛选条件封装
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQueryRequest.getDescription();
        // status为-1默认筛选所有数据（为其他状态则进行拼接）
        int status = interfaceInfoQueryRequest.getStatus();

        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 封装筛选条件（按需封装）
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        // 根据状态进行分类检索,如果传入status为-1则默认检索所有内容,如果为其他状态则默认拼接SQL
        queryWrapper.eq(status != -1, "status", status);
        // 获取分页数据
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }


    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<InterfaceInfo>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
//        if (interfaceInfoQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        interfaceInfoQueryRequest.setUserId(loginUser.getId());
//        long current = interfaceInfoQueryRequest.getCurrent();
//        long size = interfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
//                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
        return null;
    }


    /**
     * 发布接口:
     * 1.校验接口是否存在
     * 2.判断接口是否可以被调用
     * 3.修改数据库中接口信息状态为1
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) throws UnsupportedEncodingException {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.校验该接口是否存在
        long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 2.判断该接口是否可以调用 todo 模拟接口调用：确认接口是否响应正常 （此处默认放行）
        /*
        // 创建一个User对象(模拟数据)
        com.noob.apiclientsdk.model.User user = new com.noob.apiclientsdk.model.User();
        // 设置user对象的username属性为"test"
        user.setUsername("test");
        // 通过apiClient的getUsernameByPost方法传入user对象，并将返回的username赋值给username变量
//        String username = apiClient.getUserNameByPostBySign(user); // 直接调用api-platform-interface接口
        String username = apiClient.getUserNameByGateway(user); // 通过api-platform-gateway网关调用api-platform-interface接口


        // 如果username为空或空白字符串
        if (StringUtils.isBlank(username)) {
            // 抛出系统错误的业务异常，表示系统内部异常，并附带错误信息"接口验证失败"
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
        }
        */

        // 3.修改接口数据库中的状态字段为 1
        // 创建一个InterfaceInfo对象
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        // 设置interfaceInfo的id属性为id
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);

        // 4.返回响应数据
        return ResultUtils.success(result);
    }


    /**
     * 更新状态:
     *
     * @param handleInterfaceInfoStatusRequest
     * @param request
     * @return
     */
    @PostMapping("/handleInterfaceInfoStatus")
    public BaseResponse<Boolean> handleInterfaceInfoStatus(@RequestBody HandleInterfaceInfoStatusRequest handleInterfaceInfoStatusRequest) {
        ThrowUtils.throwIf(handleInterfaceInfoStatusRequest == null, ErrorCode.PARAMS_ERROR);
        // 校验参数信息
        long interfaceId = handleInterfaceInfoStatusRequest.getId();
        int status = handleInterfaceInfoStatusRequest.getStatus();
        ThrowUtils.throwIf(interfaceId < 0, ErrorCode.PARAMS_ERROR);
        boolean result = interfaceInfoService.handleInterfaceInfoStatus(interfaceId, status);
        // 返回响应数据
        return ResultUtils.success(result);
    }


    /**
     * 测试调用接口
     *
     * @param interfaceInfoInvokeRequest 封装一个参数InterfaceInfoInvokeRequest用于接收前端请求调用的参数信息
     * @return 返回结果直接将响应结果返回，因为实际情况并不确定接口的返回值到底是什么，只需要将结果数据返回即可
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest) throws UnsupportedEncodingException {
        // 检查请求对象是否为空或者接口id是否小于等于0
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取接口id
        long id = interfaceInfoInvokeRequest.getId();
        // 1.判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 2.检查接口状态是否为下线状态
        if (oldInterfaceInfo.getStatus() == InterfaceInfoEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }

        // 3.调用接口信息获取响应数据
        String res = interfaceInfoService.invokeInterfaceInfo();

        // 返回成功响应，并包含调用结果
        return ResultUtils.success(res);
    }

}
