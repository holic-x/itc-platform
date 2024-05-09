package com.noob.module.admin.cms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.common.*;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.service.UserService;
import com.noob.module.admin.cms.constant.NotificationConstant;
import com.noob.module.admin.cms.model.dto.NotificationAddRequest;
import com.noob.module.admin.cms.model.dto.NotificationQueryRequest;
import com.noob.module.admin.cms.model.dto.NotificationStatusUpdateRequest;
import com.noob.module.admin.cms.model.dto.NotificationUpdateRequest;
import com.noob.module.admin.cms.model.entity.Notification;
import com.noob.module.admin.cms.model.vo.NotificationVO;
import com.noob.module.admin.cms.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * CMS: 通告管理
 */
//@Api(tags = {"admin","NotificationController"})
@RestController
@RequestMapping("/admin/cms/notification")
@Slf4j
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @Resource
    private UserService userService;

    // region 增删改查
    /**
     * 创建
     * @param notificationAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addNotification(@RequestBody NotificationAddRequest notificationAddRequest) {
        if (notificationAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Notification notification = new Notification();
        BeanUtils.copyProperties(notificationAddRequest, notification);

        // 设置状态
        notification.setStatus(NotificationConstant.NOTICE_STATUS_OFF);
        notificationService.validNotification(notification, true);

        // 获取当前登陆用户
        Date currentTime = new Date();
        notification.setCreater(ShiroUtil.getCurrentUserId());
        notification.setUpdater(ShiroUtil.getCurrentUserId());
        notification.setCreateTime(currentTime);
        notification.setUpdateTime(currentTime);

        // 新增
        boolean result = notificationService.save(notification);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newNotificationId = notification.getId();
        return ResultUtils.success(newNotificationId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteNotification(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        Notification oldNotification = notificationService.getById(id);
        ThrowUtils.throwIf(oldNotification == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldNotification.getCreater().equals(currentUser.getId()) && !ShiroUtil.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = notificationService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param notificationUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateNotification(@RequestBody NotificationUpdateRequest notificationUpdateRequest) {
        if (notificationUpdateRequest == null || notificationUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Notification notification = new Notification();
        notification.setUpdateTime(new Date());
        notification.setUpdater(ShiroUtil.getCurrentUserId());
        BeanUtils.copyProperties(notificationUpdateRequest, notification);

        // 参数校验
        notificationService.validNotification(notification, false);
        long id = notificationUpdateRequest.getId();
        // 判断是否存在
        Notification oldNotification = notificationService.getById(id);
        ThrowUtils.throwIf(oldNotification == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = notificationService.updateById(notification);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<NotificationVO> getNotificationVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(notificationService.getNotificationVOId(id));
    }

    /**
     * 根据 domain 获取
     *
     * @param domain
     * @return
     */
    @GetMapping("/getNotificationVOByDomain")
    public BaseResponse<NotificationVO> getNotificationVOByDomain(String domain) {
        ThrowUtils.throwIf(StringUtils.isBlank(domain),ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(notificationService.getNotificationVOByDomain(domain));
    }


    /**
     * 根据筛选条件获取列表
     *
     * @param notificationQueryRequest
     * @return
     */
    @PostMapping("/list/vo")
    public BaseResponse<List<NotificationVO>> getNotificationVOByCond(@RequestBody NotificationQueryRequest notificationQueryRequest) {
        long current = notificationQueryRequest.getCurrent();
        long size = notificationQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取分页信息
        return ResultUtils.success(notificationService.getVOByCond(notificationQueryRequest));
    }

    /**
     * 分页获取列表（自定义SQL处理）
     *
     * @param notificationQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<NotificationVO>> getNotificationVOByPage(@RequestBody NotificationQueryRequest notificationQueryRequest) {
        long current = notificationQueryRequest.getCurrent();
        long size = notificationQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取分页信息
        return ResultUtils.success(notificationService.getVOByPage(notificationQueryRequest));
    }


    // endregion

    /**
     * 更新数据状态
     *
     * @param notificationStatusUpdateRequest
     * @return
     */
    @PostMapping("/handleNotificationStatus")
    public BaseResponse<Boolean> handleNotificationStatus(@RequestBody NotificationStatusUpdateRequest notificationStatusUpdateRequest) {
        if (notificationStatusUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long notificationId = notificationStatusUpdateRequest.getId();
        Notification findNotification = notificationService.getById(notificationId);
        if (findNotification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"数据信息不存在");
        }

        // 根据指定操作更新数据信息（此处根据操作类型更新数据状态信息）
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setUpdateTime(new Date());
        notification.setUpdater(ShiroUtil.getCurrentUserId());
        String operType = notificationStatusUpdateRequest.getOperType();
        Integer currentUserStatus = notification.getStatus();
        if("on".equals(operType)){
            // 校验当前状态，避免重复操作
            if(currentUserStatus== NotificationConstant.NOTICE_STATUS_ON){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前数据已发布，请勿重复操作");
            }
            notification.setStatus(NotificationConstant.NOTICE_STATUS_ON);
            notificationService.updateById(notification);
        }else if("off".equals(operType)){
            notification.setStatus(NotificationConstant.NOTICE_STATUS_OFF);
            notificationService.updateById(notification);
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
     * @return
     */
    @PostMapping("/batchDeleteNotification")
    public BaseResponse<Boolean> batchDeleteNotification(@RequestBody BatchDeleteRequest batchDeleteRequest) {
        if (batchDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 执行批量操作
        List<Long> idList = batchDeleteRequest.getIdList();
        if(idList == null || idList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"指定操作列表不能为空");
        }
        // 批量删除
        boolean b = notificationService.removeBatchByIds(idList);
        return ResultUtils.success(b);
    }

}
