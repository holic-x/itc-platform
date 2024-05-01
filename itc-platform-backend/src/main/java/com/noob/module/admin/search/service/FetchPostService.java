package com.noob.module.admin.search.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.base.post.model.dto.post.PostQueryRequest;
import com.noob.module.admin.base.post.model.entity.Post;
import com.noob.module.admin.search.model.dto.FetchPostQueryRequest;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.search.model.vo.FetchPostVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author hahabibu
* @description 针对表【fetch_post(帖子)】的数据库操作Service
* @createDate 2024-04-27 15:22:45
*/
public interface FetchPostService extends IService<FetchPost> {

    /**
     * 获取查询条件
     *
     * @param fetchPostQueryRequest
     * @return
     */
    QueryWrapper<FetchPost> getQueryWrapper(FetchPostQueryRequest fetchPostQueryRequest);

    /**
     * 分页获取帖子封装
     *
     * @param fetchPostPage
     * @return
     */
    Page<FetchPostVO> getFetchPostVOPage(Page<FetchPost> fetchPostPage);

    /**
     * 从 ES 查询
     *
     * @param fetchPostQueryRequest
     * @return
     */
    Page<FetchPost> searchFromEs(FetchPostQueryRequest fetchPostQueryRequest);

    /**
     * 模拟文章抓取
     * @return
     */
    boolean modFetchPost();

}
