package com.noob.module.dataInfo.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 */
@Data
public class DataInfoUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     *数据类型
     */
    private String dataType;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据内容
     */
    private String dataContent;

    /**
     * 数据状态（0-待启用；1-已发布）
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}