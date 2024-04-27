package com.noob.module.dataInfo.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
public class DataInfoAddRequest implements Serializable {

    /**
     * 数据名称
     */
    private String dataName;

    /**
     *数据类型
     */
    private String dataType;

    /**
     * 数据内容
     */
    private String dataContent;

    private static final long serialVersionUID = 1L;
}