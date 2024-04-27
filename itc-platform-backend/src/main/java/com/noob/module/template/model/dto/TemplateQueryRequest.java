package com.noob.module.template.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.noob.framework.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateQueryRequest extends PageRequest implements Serializable {

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 模板状态（0-待启用；1-已发布）
     */
    private Integer status;

    /**
     * 创建者
     */
    private Long creater;


    private static final long serialVersionUID = 1L;
}