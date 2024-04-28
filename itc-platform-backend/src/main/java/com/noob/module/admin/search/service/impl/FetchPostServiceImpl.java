package com.noob.module.admin.search.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.framework.constant.CommonConstant;
import com.noob.framework.utils.SqlUtils;
import com.noob.module.admin.search.model.dto.FetchPostQueryRequest;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.noob.module.admin.search.model.vo.FetchPostVO;
import com.noob.module.admin.search.service.FetchPostService;
import com.noob.module.front.search.mapper.FetchPostMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author hahabibu
* @description 针对表【fetch_fetchPost(帖子)】的数据库操作Service实现
* @createDate 2024-04-27 15:22:45
*/
@Service
public class FetchPostServiceImpl extends ServiceImpl<FetchPostMapper, FetchPost>
    implements FetchPostService{

    /**
     * 获取查询包装类
     *
     * @param fetchPostQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<FetchPost> getQueryWrapper(FetchPostQueryRequest fetchPostQueryRequest) {
        QueryWrapper<FetchPost> queryWrapper = new QueryWrapper<>();
        if (fetchPostQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = fetchPostQueryRequest.getSearchText();
        String sortField = fetchPostQueryRequest.getSortField();
        String sortOrder = fetchPostQueryRequest.getSortOrder();
        Long id = fetchPostQueryRequest.getId();
        String title = fetchPostQueryRequest.getTitle();
        String content = fetchPostQueryRequest.getContent();
        List<String> tagList = fetchPostQueryRequest.getTags();
        Long userId = fetchPostQueryRequest.getUserId();
        Long notId = fetchPostQueryRequest.getNotId();
        Integer status = fetchPostQueryRequest.getStatus();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(status!=null, "status", status);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<FetchPostVO> getFetchPostVOPage(Page<FetchPost> fetchPostPage, HttpServletRequest request) {
        // 循环处理，将obj转化为对应的vo对象
        List<FetchPost> fetchPostList = fetchPostPage.getRecords();
        Page<FetchPostVO> fetchPostVOPage = new Page<>(fetchPostPage.getCurrent(), fetchPostPage.getSize(), fetchPostPage.getTotal());
        if (CollUtil.isEmpty(fetchPostList)) {
            return fetchPostVOPage;
        }
        // 填充信息
        List<FetchPostVO> fetchPostVOList = fetchPostList.stream().map(fetchPost -> {
            FetchPostVO fetchPostVO = FetchPostVO.objToVo(fetchPost);
            return fetchPostVO;
        }).collect(Collectors.toList());
        fetchPostVOPage.setRecords(fetchPostVOList);
        return fetchPostVOPage;
    }

    /**
     * 文章数据抓取流程：
     * 1.分析数据源，获取数据（此处用hutool工具类请求接口获取数据）
     * 2.得到数据，解析数据
     * 3.对比数据，入库处理
     */
    @Override
    public boolean modFetchPost() {
        // 1. 获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
//        System.out.println(result);

        // 2. json转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<FetchPost> fetchPostList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            FetchPost fetchPost = new FetchPost();
            fetchPost.setTitle(tempRecord.getStr("title"));
            fetchPost.setCategory(tempRecord.getStr("category"));
            fetchPost.setContent(tempRecord.getStr("content"));

            // 统计数据处理
            fetchPost.setViewNum(tempRecord.getInt("viewNum"));
            fetchPost.setFavourNum(tempRecord.getInt("favourNum"));
            fetchPost.setThumbNum(tempRecord.getInt("thumbNum"));
            fetchPost.setCommentNum(tempRecord.getInt("commentNum"));

            // 标签数组处理
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            fetchPost.setTags(JSONUtil.toJsonStr(tagList));

            // 创建者信息处理(抓取的用户信息存储的是一个JSON数据)
            String fetchUserInfo = tempRecord.getStr("user");
            Map userInfo = JSONUtil.parseObj(fetchUserInfo);
            fetchPost.setUserId(String.valueOf(userInfo.get("id")));
            fetchPost.setUserName(String.valueOf(userInfo.get("userName")));
            fetchPost.setUserInfo(fetchUserInfo);

            // 设置文章状态
            fetchPost.setStatus(0);

            // 将封装好的文章信息装入列表
            fetchPostList.add(fetchPost);
        }

//        System.out.println(fetchPostList);

        // 3. 数据入库
        boolean res = saveBatch(fetchPostList);
        return res;
    }



}




