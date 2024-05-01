package com.noob.module.admin.cms.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 *
 */
@Data
public class NotificationAddRequest implements Serializable {

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