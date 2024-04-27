package com.noob.framework.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除请求（删除请求实体定义，统一删除请求变量为id，减少前后端数据交互对接负担）
 */
@Data
public class BatchDeleteRequest implements Serializable {

    /**
     * 要处理的id列表
     */
    private List<Long> idList;

    private static final long serialVersionUID = 1L;
}