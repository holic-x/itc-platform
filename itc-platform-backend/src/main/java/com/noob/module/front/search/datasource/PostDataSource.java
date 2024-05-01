package com.noob.module.front.search.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.common.ResultUtils;
import com.noob.module.admin.base.post.model.dto.post.PostQueryRequest;
import com.noob.module.admin.base.post.model.entity.Post;
import com.noob.module.admin.base.post.model.vo.PostVO;
import com.noob.module.admin.base.post.service.PostService;
import com.noob.module.admin.search.model.dto.FetchPostQueryRequest;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.noob.module.admin.search.model.vo.FetchPostVO;
import com.noob.module.admin.search.service.FetchPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName PostDataSource
 * @Description 文章信息数据源服务
 * @Author holic-x
 * @Date 2024/4/27 11:29
 */
@Service
@Slf4j
public class PostDataSource implements DataSource<FetchPostVO> {

    @Resource
    private FetchPostService fetchPostService;

    @Override
    public Page<FetchPostVO> doSearch(String searchText, int pageNum, int pageSize) {
        FetchPostQueryRequest fetchPostQueryRequest = new FetchPostQueryRequest();
        fetchPostQueryRequest.setSearchText(searchText);
        fetchPostQueryRequest.setCurrent(pageNum);
        fetchPostQueryRequest.setPageSize(pageSize);
        /**
         * RequestContextHolder 获取请求信息：此处涉及到一个问题，因为request是游离这个接口规范以外的所需参数，因此在处理的时候要考虑两个方面的问题
         * 1.不满足规范考虑剔除，对接参数需求额外提供接口进行处理
         * 2.尽量自主获取到参数信息，此处借助RequestContextHolder获取请求信息从而拿到所需数据，但也会引申一个问题：当请求来源不同的时候这个request可能和系统所需的有所出入
         * 3.修改规范：确认其他接口是否也是需要这个参数，但这个改造成本可能在后期会显得大，因为一些现有的接口已经按照既定规范执行，唯恐牵一发动全身
         */
//        ServletRequestAttributes servletRequestAttributes =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 从ES中获取数据
        Page<FetchPost> fetchPostPage = fetchPostService.searchFromEs(fetchPostQueryRequest);
        // 转化为对应的VO
        return fetchPostService.getFetchPostVOPage(fetchPostPage);
    }
}
