package com.noob.module.admin.cms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.cms.model.dto.NotificationQueryRequest;
import com.noob.module.admin.cms.model.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noob.module.admin.cms.model.vo.NotificationVO;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
* @author hahabibu
* @description 针对表【notification】的数据库操作Mapper
* @createDate 2024-05-01 13:57:46
* @Entity com.noob.module.admin.cms.model.entity.Notification
*/
public interface NotificationMapper extends BaseMapper<Notification> {

    // 根据ID查找数据
    NotificationVO getVOById(@Param("noticeId") long noticeId);



    // 根据筛选条件查找数据
    List<NotificationVO> getVOByCond(@Param("params") NotificationQueryRequest dataInfoQueryRequest);


    // 分页查找数据
    Page<NotificationVO> getVOByPage(@Param("params") NotificationQueryRequest dataInfoQueryRequest, Page<T> page);
    

}




