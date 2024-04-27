package com.noob.module.template.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.noob.module.base.model.vo.UserVO;
import com.noob.module.template.model.entity.Template;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 模板视图
 *
 */
@Data
public class TemplateVO implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
    private UserVO creater;

    /**
     * 修改者
     */
    private UserVO updater;

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
    private Integer isDelete;

    /**
     * 模板状态（0-待启用；1-已发布）
     */
    private Integer status;
    

    /**
     * 包装类转对象
     *
     * @param templateVO
     * @return
     */
    public static Template voToObj(TemplateVO templateVO) {
        if (templateVO == null) {
            return null;
        }
        Template template = new Template();
        BeanUtils.copyProperties(templateVO, template);
        return template;
    }

    /**
     * 对象转包装类
     *
     * @param template
     * @return
     */
    public static TemplateVO objToVo(Template template) {
        if (template == null) {
            return null;
        }
        TemplateVO templateVO = new TemplateVO();
        BeanUtils.copyProperties(template, templateVO);
        return templateVO;
    }
}
