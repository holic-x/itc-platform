package com.noob.module.base.model.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章账号状态更新请求
 *
 */
@Data
public class PostStatusUpdateRequest implements Serializable {


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