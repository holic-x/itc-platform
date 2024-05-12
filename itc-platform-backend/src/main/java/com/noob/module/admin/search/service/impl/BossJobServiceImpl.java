package com.noob.module.admin.search.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.module.admin.search.model.dto.BossJobQueryRequest;
import com.noob.module.admin.search.model.entity.BossJob;
import com.noob.module.admin.search.model.vo.BossJobVO;
import com.noob.module.admin.search.service.BossJobService;
import com.noob.module.admin.search.mapper.BossJobMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author hahabibu
* @description 针对表【boss_jd_spider_job】的数据库操作Service实现
* @createDate 2024-05-12 20:15:44
*/
@Service
public class BossJobServiceImpl extends ServiceImpl<BossJobMapper, BossJob>
    implements BossJobService{

    @Resource
    private BossJobMapper bossJobMapper;

    @Override
    public BossJobVO getVOById(long id) {
        return bossJobMapper.getVOById(id);
    }

    @Override
    public Page<BossJobVO> getVOByPage(BossJobQueryRequest bossJobQueryRequest) {
        long current = bossJobQueryRequest.getCurrent();
        long size = bossJobQueryRequest.getPageSize();
        Page<T> page = new Page<>(current, size);
        Page<BossJobVO> bossJobVOPage = bossJobMapper.getVOByPage(bossJobQueryRequest,page);
        return bossJobVOPage;
    }
}




