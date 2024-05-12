package com.noob.module.admin.search.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 模板视图
 *
 */
@Data
public class BossJobVO implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

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
     * 详情页链接
     */
    private String link;

    /**
     * 公司名
     */
    private String company;

    /**
     * 职位描述
     */
    private String descr;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
