package com.noob.module.front.search.es.job;

import cn.hutool.core.collection.CollUtil;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.noob.module.front.search.es.dao.FetchPostEsDao;
import com.noob.module.front.search.mapper.FetchPostMapper;
import com.noob.module.front.search.es.dto.FetchPostEsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步帖子到 es
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class IncSyncFetchPostToEs {

    @Resource
    private FetchPostMapper fetchPostMapper;

    @Resource
    private FetchPostEsDao fetchPostEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        List<FetchPost> postList = fetchPostMapper.listPostWithDelete(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(postList)) {
            log.info("no inc post");
            return;
        }
        List<FetchPostEsDTO> postEsDTOList = postList.stream()
                .map(FetchPostEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("IncSyncFetchPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            fetchPostEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("IncSyncFetchPostToEs end, total {}", total);
    }
}
