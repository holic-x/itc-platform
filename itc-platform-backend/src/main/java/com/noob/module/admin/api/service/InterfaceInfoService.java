package com.noob.module.admin.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.api.model.entity.InterfaceInfo;

/**
* @author hahabibu
* @description 针对表【interface_info(接口信息)】的数据库操作Service
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

}
