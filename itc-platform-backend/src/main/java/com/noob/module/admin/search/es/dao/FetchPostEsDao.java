package com.noob.module.admin.search.es.dao;

import com.noob.module.admin.search.es.dto.FetchPostEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 帖子 ES 操作
 */
public interface FetchPostEsDao extends ElasticsearchRepository<FetchPostEsDTO, Long> {

    // 根据用户id查找帖子信息（此处借助ElasticsearchRepository按照指定规则定义方法名，其会自动生成符合既定规则的方法实现，无需额外自定义实现）
    List<FetchPostEsDao> findByUserId(Long userId);
}