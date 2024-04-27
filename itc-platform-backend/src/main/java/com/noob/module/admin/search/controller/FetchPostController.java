package com.noob.module.admin.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.*;
import com.noob.framework.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.search.model.dto.FetchPostQueryRequest;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.noob.module.admin.search.model.vo.FetchPostVO;
import com.noob.module.admin.search.service.FetchPostService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 抓取帖子接口
 */
@RestController("admin-FetchPostController")
@RequestMapping("/admin/fetchPost")
@Slf4j
public class FetchPostController {

    @Resource
    private FetchPostService fetchPostService;

    /**
     * 数据抓取（通过爬虫从指定网站获取数据）
     *
     * @param request
     * @return
     */
    @PostMapping("/dataCapture")
    public BaseResponse<Boolean> dataCapture(HttpServletRequest request) {
        // 调用爬虫服务，模拟爬取网站文章信息，随后保存到数据库中
        boolean res = fetchPostService.modFetchPost();
        return ResultUtils.success(res);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFetchPost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        FetchPost oldFetchPost = fetchPostService.getById(id);
        ThrowUtils.throwIf(oldFetchPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 执行删除操作
        boolean b = fetchPostService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 分页获取列表
     *
     * @param fetchPostQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<FetchPostVO>> listFetchPostVoByPageForAdmin(@RequestBody FetchPostQueryRequest fetchPostQueryRequest,
                                                                       HttpServletRequest request) {
        long current = fetchPostQueryRequest.getCurrent();
        long size = fetchPostQueryRequest.getPageSize();
        Page<FetchPost> fetchPostPage = fetchPostService.page(new Page<>(current, size),
                fetchPostService.getQueryWrapper(fetchPostQueryRequest));
        return ResultUtils.success(fetchPostService.getFetchPostVOPage(fetchPostPage,request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param fetchPostQueryRequest
     * @param request
     * @return
     */
    /*
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<FetchPostVO>> searchFetchPostVOByPage(@RequestBody FetchPostQueryRequest fetchPostQueryRequest,
            HttpServletRequest request) {
        long size = fetchPostQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<FetchPost> fetchPostPage = fetchPostService.searchFromEs(fetchPostQueryRequest);
        return ResultUtils.success(fetchPostService.getFetchPostVOPage(fetchPostPage, request));
    }
    */


    /**
     * 更新文章状态
     *
     * @param fetchPostStatusUpdateRequest
     * @param request
     * @return
     */
    /*
    @PostMapping("/handleFetchPostStatus")
    public BaseResponse<Boolean> handleFetchPostStatus(@RequestBody FetchPostStatusUpdateRequest fetchPostStatusUpdateRequest,
                                                  HttpServletRequest request) {
        if (fetchPostStatusUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long fetchPostId = fetchPostStatusUpdateRequest.getId();
        FetchPost findFetchPost = fetchPostService.getById(fetchPostId);
        if (findFetchPost == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"文章信息不存在");
        }

        // 根据指定操作更新文章信息（此处根据操作类型更新文章状态信息）
        FetchPost fetchPost = new FetchPost();
        fetchPost.setId(fetchPostId);
        fetchPost.setUpdateTime(new Date());
        String operType = fetchPostStatusUpdateRequest.getOperType();
        Integer currentUserStatus = fetchPost.getStatus();
        if("publish".equals(operType)){
            // 校验当前状态，避免重复发布
            if(currentUserStatus== FetchPostConstant.POST_STATUS_PUBLISH){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"当前文章已发布，请勿重复操作");
            }
            fetchPost.setStatus(FetchPostConstant.POST_STATUS_PUBLISH);
            fetchPostService.updateById(fetchPost);
        }else if("draft".equals(operType)){
            fetchPost.setStatus(FetchPostConstant.POST_STATUS_DRAFT);
            fetchPostService.updateById(fetchPost);
        }else {
            // 其余操作类型则不允许操作
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"指定操作类型错误，请联系管理员处理");
        }
        return ResultUtils.success(true);
    }

     */

    // --------------- 批量操作定义 ---------------

    /**
     * 批量删除文章
     *
     * @param batchDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/batchDeleteFetchPost")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteFetchPost(@RequestBody BatchDeleteRequest batchDeleteRequest, HttpServletRequest request) {
        if (batchDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 执行批量操作
        List<Long> idList = batchDeleteRequest.getIdList();
        if (idList == null || idList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "指定操作列表不能为空");
        }
        // 批量删除
        boolean b = fetchPostService.removeBatchByIds(idList);
        return ResultUtils.success(b);
    }

}
