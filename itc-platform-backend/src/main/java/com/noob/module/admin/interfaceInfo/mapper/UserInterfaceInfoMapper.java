package com.noob.module.admin.interfaceInfo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noob.module.admin.interfaceInfo.model.entity.InterfaceInfo;
import com.noob.module.admin.interfaceInfo.model.entity.UserInterfaceInfo;
import com.noob.module.front.interfaceInfo.model.dto.InterfaceInfoStatisticQueryRequest;
import com.noob.module.front.interfaceInfo.model.vo.InterfaceInfoStatisticVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author hahabibu
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @Entity com.noob.springbootinit.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    // 获取接口调用前几的接口信息
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);


}




