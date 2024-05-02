package com.noob.module.admin.api.model.dto;

import lombok.Data;

/**
 * 接口调用请求参数定义
 */
@Data
public class InterfaceInfoInvokeRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;

}
