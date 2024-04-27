package com.noob.module.dataInfo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.dataInfo.model.dto.DataInfoQueryRequest;
import com.noob.module.dataInfo.model.entity.DataInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noob.module.dataInfo.model.vo.DataInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

/**
* @author HUAWEI
* @description 针对表【data_info】的数据库操作Mapper
* @createDate 2024-04-22 09:03:07
* @Entity com.noob.module.data.model.entity.DataInfo
*/
public interface DataInfoMapper extends BaseMapper<DataInfo> {

    // 分页查找数据
    Page<DataInfoVO> getVOByPage(@Param("params") DataInfoQueryRequest dataInfoQueryRequest, Page<T> page);

}




