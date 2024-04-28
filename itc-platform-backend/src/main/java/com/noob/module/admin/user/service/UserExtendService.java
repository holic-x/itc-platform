package com.noob.module.admin.user.service;

import com.noob.module.admin.user.model.entity.UserExtend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
* @author hahabibu
* @description 针对表【user_extend】的数据库操作Service
* @createDate 2024-04-28 09:36:34
*/
public interface UserExtendService extends IService<UserExtend> {

    /**
     * 根据用户id补充用户扩展字段信息
     * @param uid
     * @param currentTime
     * @param registerChannel
     * @return
     */
    public boolean initDefaultUserExtend(long uid, Date currentTime,String registerChannel);

}
