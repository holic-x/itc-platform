package com.noob.module.front.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ResultUtils;
import com.noob.module.admin.base.user.constant.UserConstant;
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

/**
 * 抓取帖子接口(前台)
 */
@RestController("user-FetchPostController")
@RequestMapping("/fetchPost")
@Slf4j
public class FetchPostController {

    @Resource
    private FetchPostService fetchPostService;


    /**
     * 分页获取列表
     *
     * @param fetchPostQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<FetchPostVO>> listFetchPostVOByPageForUser(@RequestBody FetchPostQueryRequest fetchPostQueryRequest) {
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
}
