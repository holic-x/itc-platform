package com.noob.module.front.search.model.dto;

import com.noob.framework.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SearchRequest
 * @Description 检索请求参数
 * @Author holic-x
 * @Date 2024/4/27 11:05
 */
@Data
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 检索类型
     */
    private String searchType;

    private static final long serialVersionUID = 1L;
}