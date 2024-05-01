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
import com.noob.module.admin.search.es.dao.FetchPostEsDao;
import com.noob.module.admin.search.model.dto.FetchPostQueryRequest;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.noob.module.admin.search.model.vo.FetchPostVO;
import com.noob.module.admin.search.service.FetchPostService;
import com.noob.module.front.search.mapper.FetchPostMapper;
import com.noob.module.admin.search.es.dto.FetchPostEsDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author hahabibu
* @description 针对表【fetch_fetchPost(帖子)】的数据库操作Service实现
* @createDate 2024-04-27 15:22:45
*/
@Slf4j
@Service
public class FetchPostServiceImpl extends ServiceImpl<FetchPostMapper, FetchPost>
    implements FetchPostService{

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Resource
    private FetchPostEsDao fetchPostEsDao;


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
        String sortField = fetchPostQueryRequest.getSortField();
        String sortOrder = fetchPostQueryRequest.getSortOrder();
        Long id = fetchPostQueryRequest.getId();

        String searchText = fetchPostQueryRequest.getSearchText();
        String userName = fetchPostQueryRequest.getUserName();

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

        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.and(qw -> qw.like("userName", userName));
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
    public Page<FetchPostVO> getFetchPostVOPage(Page<FetchPost> fetchPostPage) {
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

    @Override
    public Page<FetchPost> searchFromEs(FetchPostQueryRequest fetchPostQueryRequest) {

        // 模拟从ES中获取文章数据
        Long id = fetchPostQueryRequest.getId();
        Long notId = fetchPostQueryRequest.getNotId();
        String searchText = fetchPostQueryRequest.getSearchText();
        String title = fetchPostQueryRequest.getTitle();
        String content = fetchPostQueryRequest.getContent();
        List<String> tagList = fetchPostQueryRequest.getTags();
        List<String> orTagList = fetchPostQueryRequest.getOrTags();
        Long userId = fetchPostQueryRequest.getUserId();
        // es 起始页为 0
        long current = fetchPostQueryRequest.getCurrent() - 1;
        long pageSize = fetchPostQueryRequest.getPageSize();
        String sortField = fetchPostQueryRequest.getSortField();
        String sortOrder = fetchPostQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 必须包含所有标签
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 包含任何一个标签即可
        if (CollUtil.isNotEmpty(orTagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : orTagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<FetchPostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, FetchPostEsDTO.class);

        log.info("从ES中检索数据记录总条数为：{}",searchHits.getTotalHits());

        // sign 动静分离：先从ES检索满足条件的记录，然后再从数据库中关联其最新的记录信息
        // 封装查询数据
        Page<FetchPost> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<FetchPost> resourceList = new ArrayList<>();

        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<FetchPostEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> postIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<FetchPost> fetchPostList = baseMapper.selectBatchIds(postIdList);
            if (fetchPostList != null) {
                Map<Long, List<FetchPost>> idPostMap = fetchPostList.stream().collect(Collectors.groupingBy(FetchPost::getId));
                postIdList.forEach(postId -> {
                    if (idPostMap.containsKey(postId)) {
                        resourceList.add(idPostMap.get(postId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(postId), FetchPostEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        return page;
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

    @Override
    public boolean pushFullToES() {
        // 获取文章全量数据
        List<FetchPost> fetchPostList = this.list();
        // 列表为空没有数据要推送
        if (CollUtil.isEmpty(fetchPostList)) {
            return true;
        }
        List<FetchPostEsDTO> fetchPostEsDTOList = fetchPostList.stream().map(FetchPostEsDTO::objToDto).collect(Collectors.toList());

        // 批处理（每500条保存一次）
        final int pageSize = 500;
        int total = fetchPostEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            fetchPostEsDao.saveAll(fetchPostEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end, total {}", total);
        return true;
    }


}




