package com.noob.module.dataInfo.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.*;
import com.noob.framework.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.base.model.entity.User;
import com.noob.module.base.service.UserService;
import com.noob.module.dataInfo.constant.DataInfoConstant;
import com.noob.module.dataInfo.model.dto.DataInfoAddRequest;
import com.noob.module.dataInfo.model.dto.DataInfoQueryRequest;
import com.noob.module.dataInfo.model.dto.DataInfoStatusUpdateRequest;
import com.noob.module.dataInfo.model.dto.DataInfoUpdateRequest;
import com.noob.module.dataInfo.model.entity.DataInfo;
import com.noob.module.dataInfo.model.vo.DataInfoVO;
import com.noob.module.dataInfo.service.DataInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 数据接口
 */
@RestController
@RequestMapping("/dataInfo")
@Slf4j
public class DataInfoController {

    @Resource
    private DataInfoService dataInfoService;

    @Resource
    private UserService userService;

    // region 增删改查
    /**
     * 创建
     * @param dataInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addDataInfo(@RequestBody DataInfoAddRequest dataInfoAddRequest, HttpServletRequest request) {
        if (dataInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DataInfo dataInfo = new DataInfo();
        BeanUtils.copyProperties(dataInfoAddRequest, dataInfo);

        // 设置状态
        dataInfo.setStatus(DataInfoConstant.TEMPLATE_STATUS_DRAFT);
        dataInfoService.validDataInfo(dataInfo, true);

        User loginUser = userService.getLoginUser(request);
        dataInfo.setCreater(loginUser.getId());
        dataInfo.setUpdater(loginUser.getId());
        dataInfo.setCreateTime(new Date());
        dataInfo.setUpdateTime(new Date());

        // 新增
        boolean result = dataInfoService.save(dataInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newDataInfoId = dataInfo.getId();
        return ResultUtils.success(newDataInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteDataInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        DataInfo oldDataInfo = dataInfoService.getById(id);
        ThrowUtils.throwIf(oldDataInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldDataInfo.getCreater().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = dataInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param dataInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDataInfo(@RequestBody DataInfoUpdateRequest dataInfoUpdateRequest) {
        if (dataInfoUpdateRequest == null || dataInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DataInfo dataInfo = new DataInfo();
        dataInfo.setUpdateTime(new Date());
        BeanUtils.copyProperties(dataInfoUpdateRequest, dataInfo);

        // 参数校验
        dataInfoService.validDataInfo(dataInfo, false);
        long id = dataInfoUpdateRequest.getId();
        // 判断是否存在
        DataInfo oldDataInfo = dataInfoService.getById(id);
        ThrowUtils.throwIf(oldDataInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = dataInfoService.updateById(dataInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<DataInfoVO> getDataInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DataInfo dataInfo = dataInfoService.getById(id);
        if (dataInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(dataInfoService.getDataInfoVO(dataInfo, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param dataInfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<DataInfo>> listDataInfoByPage(@RequestBody DataInfoQueryRequest dataInfoQueryRequest) {
        long current = dataInfoQueryRequest.getCurrent();
        long size = dataInfoQueryRequest.getPageSize();
        Page<DataInfo> dataInfoPage = dataInfoService.page(new Page<>(current, size),
                dataInfoService.getQueryWrapper(dataInfoQueryRequest));
        return ResultUtils.success(dataInfoPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param dataInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<DataInfoVO>> listDataInfoVOByPage(@RequestBody DataInfoQueryRequest dataInfoQueryRequest,
                                                               HttpServletRequest request) {
        long current = dataInfoQueryRequest.getCurrent();
        long size = dataInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<DataInfo> dataInfoPage = dataInfoService.page(new Page<>(current, size),
                dataInfoService.getQueryWrapper(dataInfoQueryRequest));
        return ResultUtils.success(dataInfoService.getDataInfoVOPage(dataInfoPage, request));
    }



    /**
     * 分页获取列表（自定义SQL处理）
     *
     * @param dataInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/listByPage")
    public BaseResponse<Page<DataInfoVO>> listByPage(@RequestBody DataInfoQueryRequest dataInfoQueryRequest,
                                                               HttpServletRequest request) {
        // 获取分页信息
        return ResultUtils.success(dataInfoService.getVOByPage(dataInfoQueryRequest));
    }


    // endregion

    /**
     * 更新数据状态
     *
     * @param dataInfoStatusUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/handleDataInfoStatus")
    public BaseResponse<Boolean> handleDataInfoStatus(@RequestBody DataInfoStatusUpdateRequest dataInfoStatusUpdateRequest,
                                                      HttpServletRequest request) {
        if (dataInfoStatusUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long dataInfoId = dataInfoStatusUpdateRequest.getId();
        DataInfo findDataInfo = dataInfoService.getById(dataInfoId);
        if (findDataInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"数据信息不存在");
        }

        // 根据指定操作更新数据信息（此处根据操作类型更新数据状态信息）
        DataInfo dataInfo = new DataInfo();
        dataInfo.setId(dataInfoId);
        dataInfo.setUpdateTime(new Date());
        String operType = dataInfoStatusUpdateRequest.getOperType();
        Integer currentUserStatus = dataInfo.getStatus();
        if("publish".equals(operType)){
            // 校验当前状态，避免重复发布
            if(currentUserStatus== DataInfoConstant.TEMPLATE_STATUS_PUBLISH){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前数据已发布，请勿重复操作");
            }
            dataInfo.setStatus(DataInfoConstant.TEMPLATE_STATUS_PUBLISH);
            dataInfoService.updateById(dataInfo);
        }else if("draft".equals(operType)){
            dataInfo.setStatus(DataInfoConstant.TEMPLATE_STATUS_DRAFT);
            dataInfoService.updateById(dataInfo);
        }else {
            // 其余操作类型则不允许操作
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"指定操作类型错误，请联系管理员处理");
        }
        return ResultUtils.success(true);
    }

    // --------------- 批量操作定义 ---------------
    /**
     * 批量删除数据
     *
     * @param batchDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/batchDeleteDataInfo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteDataInfo(@RequestBody BatchDeleteRequest batchDeleteRequest, HttpServletRequest request) {
        if (batchDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 执行批量操作
        List<Long> idList = batchDeleteRequest.getIdList();
        if(idList == null || idList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"指定操作列表不能为空");
        }
        // 批量删除
        boolean b = dataInfoService.removeBatchByIds(idList);
        return ResultUtils.success(b);
    }

}
