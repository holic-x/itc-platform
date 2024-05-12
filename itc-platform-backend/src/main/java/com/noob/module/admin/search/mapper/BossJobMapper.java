package com.noob.module.admin.search.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.search.model.dto.BossJobQueryRequest;
import com.noob.module.admin.search.model.entity.BossJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noob.module.admin.search.model.vo.BossJobVO;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

/**
* @author hahabibu
* @description 针对表【boss_jd_spider_job】的数据库操作Mapper
* @createDate 2024-05-12 20:15:44
* @Entity com.noob.module.admin.search.model.entity.BossJob
*/
public interface BossJobMapper extends BaseMapper<BossJob> {

    // 根据ID获取数据
    BossJobVO getVOById(@Param("id") Long id);

    // 分页查找数据
    Page<BossJobVO> getVOByPage(@Param("params") BossJobQueryRequest bossJobQueryRequest, Page<T> page);

}




