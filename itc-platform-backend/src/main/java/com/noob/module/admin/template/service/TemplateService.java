package com.noob.module.admin.template.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.template.model.vo.TemplateVO;
import com.noob.module.admin.template.model.dto.TemplateQueryRequest;
import com.noob.module.admin.template.model.entity.Template;

import javax.servlet.http.HttpServletRequest;

/**
* @author hahabibu
* @description 针对表【template】的数据库操作Service
* @createDate 2024-04-21 19:47:23
*/
public interface TemplateService extends IService<Template> {

    /**
     * 校验
     *
     * @param template
     * @param add
     */
    void validTemplate(Template template, boolean add);

    /**
     * 获取查询条件
     *
     * @param templateQueryRequest
     * @return
     */
    QueryWrapper<Template> getQueryWrapper(TemplateQueryRequest templateQueryRequest);

    /**
     * 获取封装
     *
     * @param template
     * @param request
     * @return
     */
    TemplateVO getTemplateVO(Template template, HttpServletRequest request);

    /**
     * 分页获取模板封装
     *
     * @param templatePage
     * @param request
     * @return
     */
    Page<TemplateVO> getTemplateVOPage(Page<Template> templatePage, HttpServletRequest request);

}
