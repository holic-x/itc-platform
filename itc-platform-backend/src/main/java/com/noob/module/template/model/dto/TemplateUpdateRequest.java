package com.noob.module.template.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 *
 */
@Data
public class TemplateUpdateRequest implements Serializable {

    /**
     * id
     */
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
     * 模板状态（0-待启用；1-已发布）
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}