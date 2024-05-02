package com.noob.module.admin.api.mapper;

import com.noob.module.admin.api.model.entity.UserInterfaceInfoCall;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noob.module.front.api.model.dto.InterfaceInfoStatisticQueryRequest;
import com.noob.module.front.api.model.vo.InterfaceInfoStatisticVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author hahabibu
* @description 针对表【user_interface_info_call(用户调用接口关系)】的数据库操作Mapper
* @createDate 2024-04-28 00:13:29
* @Entity com.noob.module.admin.interfaceInfo.model.entity.UserInterfaceInfoCall
*/
public interface UserInterfaceInfoCallMapper extends BaseMapper<UserInterfaceInfoCall> {

    // 获取接口统计信息
    List<InterfaceInfoStatisticVO> listInterfaceInfoStatistic(@Param(value = "params") InterfaceInfoStatisticQueryRequest interfaceInfoStatisticQueryRequest);


}




