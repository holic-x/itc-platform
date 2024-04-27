package com.noob.module.admin.template.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.constant.CommonConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.utils.SqlUtils;
import com.noob.module.admin.template.model.vo.TemplateVO;
import com.noob.module.admin.template.service.TemplateService;
import com.noob.module.admin.user.model.entity.User;
import com.noob.module.admin.user.model.vo.UserVO;
import com.noob.module.admin.user.service.UserService;
import com.noob.module.admin.template.mapper.TemplateMapper;
import com.noob.module.admin.template.model.dto.TemplateQueryRequest;
import com.noob.module.admin.template.model.entity.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author hahabibu
* @description 针对表【template】的数据库操作Service实现
* @createDate 2024-04-21 19:47:23
*/
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template>
    implements TemplateService {

    @Resource
    private UserService userService;

    @Override
    public void validTemplate(Template template, boolean add) {
        if (template == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String templateName = template.getTemplateName();
        String templateContent = template.getTemplateContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(templateName, templateContent), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(templateName) && templateName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(templateContent) && templateContent.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param templateQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Template> getQueryWrapper(TemplateQueryRequest templateQueryRequest) {
        QueryWrapper<Template> queryWrapper = new QueryWrapper<>();
        if (templateQueryRequest == null) {
            return queryWrapper;
        }
        String templateName = templateQueryRequest.getTemplateName();
        String templateContent = templateQueryRequest.getTemplateContent();
        Integer status = templateQueryRequest.getStatus();
        String sortField = templateQueryRequest.getSortField();
        String sortOrder = templateQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(templateName)) {
            queryWrapper.and(qw -> qw.like("templateName", templateName));
        }
        if (StringUtils.isNotBlank(templateContent)) {
            queryWrapper.and(qw -> qw.like("templateContent", templateContent));
        }
        queryWrapper.eq(status!=null, "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public TemplateVO getTemplateVO(Template template, HttpServletRequest request) {

        TemplateVO templateVO = TemplateVO.objToVo(template);
        // 1. 关联查询创建者、修改者信息
        Long createrId = template.getCreater();
        User creater = null;
        if (createrId != null && createrId > 0) {
            creater = userService.getById(createrId);
        }
        templateVO.setCreater(userService.getUserVO(creater));

        Long updaterId = template.getCreater();
        User updater = null;
        if (updaterId != null && createrId > 0) {
            updater = userService.getById(createrId);
        }
        templateVO.setUpdater(userService.getUserVO(updater));

        return templateVO;
    }

    @Override
    public Page<TemplateVO> getTemplateVOPage(Page<Template> templatePage, HttpServletRequest request) {
        List<Template> templateList = templatePage.getRecords();
        Page<TemplateVO> templateVOPage = new Page<>(templatePage.getCurrent(), templatePage.getSize(), templatePage.getTotal());
        if (CollUtil.isEmpty(templateList)) {
            return templateVOPage;
        }

        // 获取所有用户信息列表，根据ID封装成Map集合(避免循环搜索数据库)
        List<User> userList = userService.list();
        List<UserVO> userVOList = userService.getUserVO(userList);
        Map<Long,UserVO> userVOMap = new HashMap<>();
        for (UserVO userVO : userVOList) {
            userVOMap.put(userVO.getId(),userVO);
        }

        // 填充信息
        List<TemplateVO> templateVOList = templateList.stream().map(template -> {
            TemplateVO templateVO = TemplateVO.objToVo(template);

            // 设置创建者、修改者信息 todo 方式2：通过mapper直接构建sql语句关联查找
            Long createrId = template.getCreater();
            templateVO.setCreater( userVOMap.get(createrId));
            Long updaterId = template.getCreater();
            templateVO.setUpdater( userVOMap.get(updaterId));

            templateVO.setStatus(template.getStatus());
            return templateVO;
        }).collect(Collectors.toList());
        templateVOPage.setRecords(templateVOList);
        return templateVOPage;
    }

}




