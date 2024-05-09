package com.noob.module.admin.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.cms.model.dto.NotificationQueryRequest;
import com.noob.module.admin.cms.model.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.cms.model.vo.NotificationVO;

import java.util.List;

/**
* @author hahabibu
* @description 针对表【notification】的数据库操作Service
* @createDate 2024-05-01 13:57:46
*/
public interface NotificationService extends IService<Notification> {

    /**
     * 校验
     *
     * @param notification
     * @param add
     */
    void validNotification(Notification notification, boolean add);

    /**
     * 根据ID获取数据封装
     *
     * @param noticeId
     * @return
     */
    NotificationVO getNotificationVOId(long noticeId);

    /**
     * 根据筛选条件获取列表
     *
     * @param notificationQueryRequest
     * @return
     */
    List<NotificationVO> getVOByCond(NotificationQueryRequest notificationQueryRequest);


    /**
     * 分页获取数据封装(SQL处理)
     *
     * @param notificationQueryRequest
     * @return
     */
    Page<NotificationVO> getVOByPage(NotificationQueryRequest notificationQueryRequest);

    /**
     * 根据domain获取数据封装
     *
     * @param domain
     * @return
     */
    NotificationVO getNotificationVOByDomain(String domain);

}
