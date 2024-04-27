package com.noob.module.template.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 状态更新请求
 *
 */
@Data
public class TemplateStatusUpdateRequest implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 操作类型
     */
    private String operType;

    private static final long serialVersionUID = 1L;
}