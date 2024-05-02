package com.noob.module.admin.base.user.mapper;

import com.noob.module.admin.base.user.model.entity.UserExtend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author hahabibu
* @description 针对表【user_extend】的数据库操作Mapper
* @createDate 2024-04-28 09:36:34
* @Entity com.noob.module.admin.user.model.entity.UserExtend
*/
public interface UserExtendMapper extends BaseMapper<UserExtend> {


    UserExtend getUserExtendByUserId(long userId);

}




