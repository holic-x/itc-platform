package com.noob.module.admin.search.es.job;

import cn.hutool.core.collection.CollUtil;
import com.noob.module.admin.search.es.dao.FetchPostEsDao;
import com.noob.module.admin.search.es.dto.FetchPostEsDTO;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.noob.module.admin.search.service.FetchPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncFetchPostToEs implements CommandLineRunner {

    @Resource
    private FetchPostService fetchPostService;

    @Resource
    private FetchPostEsDao fetchPostEsDao;

    @Override
    public void run(String... args) {
        List<FetchPost> fetchPostList = fetchPostService.list();
        if (CollUtil.isEmpty(fetchPostList)) {
            return;
        }
        List<FetchPostEsDTO> fetchPostEsDTOList = fetchPostList.stream().map(FetchPostEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = fetchPostEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            fetchPostEsDao.saveAll(fetchPostEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end, total {}", total);
    }
}
