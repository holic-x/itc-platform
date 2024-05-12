package com.noob.module.admin.search.model.dto;

import com.noob.framework.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BossJobQueryRequest extends PageRequest implements Serializable {

    /**
     * 职位名称
     */
    private String name;

    /**
     * 区域
     */
    private String area;

    /**
     * 薪资范围
     */
    private String salary;

    /**
     * 公司名
     */
    private String company;

    /**
     * 职位描述
     */
    private String desc;


    private static final long serialVersionUID = 1L;
}