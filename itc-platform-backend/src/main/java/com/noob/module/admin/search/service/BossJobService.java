package com.noob.module.admin.search.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.search.model.dto.BossJobQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.search.model.entity.BossJob;
import com.noob.module.admin.search.model.vo.BossJobVO;

/**
* @author hahabibu
* @description 针对表【boss_jd_spider_job】的数据库操作Service
* @createDate 2024-05-12 20:15:44
*/
public interface BossJobService extends IService<BossJob> {



    /**
     * 获取封装
     *
     * @param id
     * @return
     */
    BossJobVO getVOById(long id);


    /**
     * 分页获取数据封装(SQL处理)
     *
     * @param bossJobQueryRequest
     * @return
     */
    Page<BossJobVO> getVOByPage(BossJobQueryRequest bossJobQueryRequest);

}
