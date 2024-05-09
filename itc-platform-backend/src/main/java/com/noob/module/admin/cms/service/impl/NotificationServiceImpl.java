package com.noob.module.admin.cms.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.constant.CommonConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.utils.SqlUtils;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.user.model.vo.UserVO;
import com.noob.module.admin.base.user.service.UserService;
import com.noob.module.admin.cms.model.dto.NotificationQueryRequest;
import com.noob.module.admin.cms.model.entity.Notification;
import com.noob.module.admin.cms.model.vo.NotificationVO;
import com.noob.module.admin.cms.service.NotificationService;
import com.noob.module.admin.cms.mapper.NotificationMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author hahabibu
* @description 针对表【notification】的数据库操作Service实现
* @createDate 2024-05-01 13:57:46
*/
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {


    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public void validNotification(Notification notification, boolean add) {
        if (notification == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String content = notification.getContent();
        String domain = notification.getDomain();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content, domain), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public NotificationVO getNotificationVOId(long noticeId) {
        return notificationMapper.getVOById(noticeId);
    }

    @Override
    public List<NotificationVO> getVOByCond(NotificationQueryRequest notificationQueryRequest) {
        List<NotificationVO> notificationVOList = notificationMapper.getVOByCond(notificationQueryRequest);
        return notificationVOList ;
    }


    @Override
    public Page<NotificationVO> getVOByPage(NotificationQueryRequest notificationQueryRequest) {
        long current = notificationQueryRequest.getCurrent();
        long size = notificationQueryRequest.getPageSize();
        Page<T> page = new Page<>(current, size);
        Page<NotificationVO> notificationVOPage = notificationMapper.getVOByPage(notificationQueryRequest,page);
        return notificationVOPage;
    }

    @Override
    public NotificationVO getNotificationVOByDomain(String domain) {
        return notificationMapper.getNotificationVOByDomain(domain);
    }
}




