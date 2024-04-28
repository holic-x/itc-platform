package com.noob.framework.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据ID请求（一般用作查询请求实体定义，查询请求变量为id，减少前后端数据交互对接负担）
 */
@Data
public class IdQueryRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}