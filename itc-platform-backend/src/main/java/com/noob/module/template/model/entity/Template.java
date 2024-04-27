package com.noob.module.template.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName template
 */
@TableName(value ="template")
@Data
public class Template implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 创建者
     */
    private Long creater;

    /**
     * 修改者
     */
    private Long updater;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除（0-未删除；1-已删除）
     */
//    @TableLogic(value = "0",delval = "1")
    @TableLogic
    private Integer isDelete;

    /**
     * 模板状态（0-待启用；1-已发布）
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}