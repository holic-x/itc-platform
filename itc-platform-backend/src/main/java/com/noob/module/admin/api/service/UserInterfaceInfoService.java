package com.noob.module.admin.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.api.model.dto.InterfaceInfoAddRequest;
import com.noob.module.admin.api.model.dto.UserInterfaceInfoAddRequest;
import com.noob.module.admin.api.model.entity.UserInterfaceInfo;

import javax.servlet.http.HttpServletRequest;

/**
* @author hahabibu
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 校验用户调用接口统计信息
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 添加用户调用接口信息
     * @param userInterfaceInfoAddRequest
     */
    long addUserInterfaceInfo(UserInterfaceInfoAddRequest userInterfaceInfoAddRequest);


    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

}
