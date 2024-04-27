package com.noob.module.dataInfo.model.dto;

import com.noob.framework.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataInfoQueryRequest extends PageRequest implements Serializable {

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

    /**
     * 数据状态（0-待启用；1-已发布）
     */
    private Integer status;

    /**
     * 创建者
     */
    private Long creater;


    private static final long serialVersionUID = 1L;
}