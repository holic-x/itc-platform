package com.noob.module.admin.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.*;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.search.model.dto.FetchPostQueryRequest;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.noob.module.admin.search.model.vo.FetchPostVO;
import com.noob.module.admin.search.service.FetchPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 抓取帖子接口(对抓取的帖子信息进行管理跟踪)
 * 帖子信息管理、ES数据同步状态跟踪
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
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFetchPost(@RequestBody DeleteRequest deleteRequest) {
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
    public BaseResponse<Page<FetchPostVO>> listFetchPostVoByPageForAdmin(@RequestBody FetchPostQueryRequest fetchPostQueryRequest,
                                                                       HttpServletRequest request) {
        long current = fetchPostQueryRequest.getCurrent();
        long size = fetchPostQueryRequest.getPageSize();
        Page<FetchPost> fetchPostPage = fetchPostService.page(new Page<>(current, size),
                fetchPostService.getQueryWrapper(fetchPostQueryRequest));
        return ResultUtils.success(fetchPostService.getFetchPostVOPage(fetchPostPage));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param fetchPostQueryRequest
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

    // --------------- 批量操作定义 ---------------

    /**
     * 批量删除文章
     *
     * @param batchDeleteRequest
     * @return
     */
    @PostMapping("/batchDeleteFetchPost")
    public BaseResponse<Boolean> batchDeleteFetchPost(@RequestBody BatchDeleteRequest batchDeleteRequest) {
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
