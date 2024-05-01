package com.noob.module.admin.api.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 * 此处对照InterfaceInfo的字段定义，根据实际请求添加的业务需求进行清理操作
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 响应参数
     */
    private String responseParams;

    /**
     * 请求类型
     */
    private String method;

}