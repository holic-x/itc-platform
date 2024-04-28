package com.noob.module.admin.template.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.*;
import com.noob.module.admin.user.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.template.model.dto.TemplateStatusUpdateRequest;
import com.noob.module.admin.template.model.dto.TemplateUpdateRequest;
import com.noob.module.admin.template.model.vo.TemplateVO;
import com.noob.module.admin.user.model.entity.User;
import com.noob.module.admin.user.service.UserService;
import com.noob.module.admin.template.constant.TemplateConstant;
import com.noob.module.admin.template.model.dto.TemplateAddRequest;
import com.noob.module.admin.template.model.dto.TemplateQueryRequest;
import com.noob.module.admin.template.model.entity.Template;
import com.noob.module.admin.template.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 模板接口
 */
@RestController
@RequestMapping("/template")
@Slf4j
public class TemplateController {

    @Resource
    private TemplateService templateService;

    @Resource
    private UserService userService;

    // region 增删改查
    /**
     * 创建
     * @param templateAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTemplate(@RequestBody TemplateAddRequest templateAddRequest, HttpServletRequest request) {
        if (templateAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Template template = new Template();
        BeanUtils.copyProperties(templateAddRequest, template);

        // 设置状态
        template.setStatus(TemplateConstant.TEMPLATE_STATUS_DRAFT);
        templateService.validTemplate(template, true);

        User loginUser = userService.getLoginUser(request);
        template.setCreater(loginUser.getId());
        template.setUpdater(loginUser.getId());
        template.setCreateTime(new Date());
        template.setUpdateTime(new Date());

        // 新增
        boolean result = templateService.save(template);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newTemplateId = template.getId();
        return ResultUtils.success(newTemplateId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTemplate(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Template oldTemplate = templateService.getById(id);
        ThrowUtils.throwIf(oldTemplate == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldTemplate.getCreater().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = templateService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param templateUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTemplate(@RequestBody TemplateUpdateRequest templateUpdateRequest) {
        if (templateUpdateRequest == null || templateUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Template template = new Template();
        template.setUpdateTime(new Date());
        BeanUtils.copyProperties(templateUpdateRequest, template);

        // 参数校验
        templateService.validTemplate(template, false);
        long id = templateUpdateRequest.getId();
        // 判断是否存在
        Template oldTemplate = templateService.getById(id);
        ThrowUtils.throwIf(oldTemplate == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = templateService.updateById(template);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<TemplateVO> getTemplateVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Template template = templateService.getById(id);
        if (template == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(templateService.getTemplateVO(template, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param templateQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Template>> listTemplateByPage(@RequestBody TemplateQueryRequest templateQueryRequest) {
        long current = templateQueryRequest.getCurrent();
        long size = templateQueryRequest.getPageSize();
        Page<Template> templatePage = templateService.page(new Page<>(current, size),
                templateService.getQueryWrapper(templateQueryRequest));
        return ResultUtils.success(templatePage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param templateQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TemplateVO>> listTemplateVOByPage(@RequestBody TemplateQueryRequest templateQueryRequest,
                                                               HttpServletRequest request) {
        long current = templateQueryRequest.getCurrent();
        long size = templateQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Template> templatePage = templateService.page(new Page<>(current, size),
                templateService.getQueryWrapper(templateQueryRequest));
        return ResultUtils.success(templateService.getTemplateVOPage(templatePage, request));
    }

    // endregion

    /**
     * 更新模板状态
     *
     * @param templateStatusUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/handleTemplateStatus")
    public BaseResponse<Boolean> handleTemplateStatus(@RequestBody TemplateStatusUpdateRequest templateStatusUpdateRequest,
                                                      HttpServletRequest request) {
        if (templateStatusUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long templateId = templateStatusUpdateRequest.getId();
        Template findTemplate = templateService.getById(templateId);
        if (findTemplate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"模板信息不存在");
        }

        // 根据指定操作更新模板信息（此处根据操作类型更新模板状态信息）
        Template template = new Template();
        template.setId(templateId);
        template.setUpdateTime(new Date());
        String operType = templateStatusUpdateRequest.getOperType();
        Integer currentUserStatus = template.getStatus();
        if("publish".equals(operType)){
            // 校验当前状态，避免重复发布
            if(currentUserStatus== TemplateConstant.TEMPLATE_STATUS_PUBLISH){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前模板已发布，请勿重复操作");
            }
            template.setStatus(TemplateConstant.TEMPLATE_STATUS_PUBLISH);
            templateService.updateById(template);
        }else if("draft".equals(operType)){
            template.setStatus(TemplateConstant.TEMPLATE_STATUS_DRAFT);
            templateService.updateById(template);
        }else {
            // 其余操作类型则不允许操作
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"指定操作类型错误，请联系管理员处理");
        }
        return ResultUtils.success(true);
    }

    // --------------- 批量操作定义 ---------------
    /**
     * 批量删除模板
     *
     * @param batchDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/batchDeleteTemplate")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteTemplate(@RequestBody BatchDeleteRequest batchDeleteRequest, HttpServletRequest request) {
        if (batchDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 执行批量操作
        List<Long> idList = batchDeleteRequest.getIdList();
        if(idList == null || idList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"指定操作列表不能为空");
        }
        // 批量删除
        boolean b = templateService.removeBatchByIds(idList);
        return ResultUtils.success(b);
    }

}
