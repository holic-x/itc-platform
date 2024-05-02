package com.noob.module.admin.cms.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 修改请求
 *
 */
@Data
public class NotificationUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;


    /**
     * 0: 关闭，1: 启用
     */
    private Integer status;

    /**
     * 域名
     */
    private String domain;

    private static final long serialVersionUID = 1L;
}