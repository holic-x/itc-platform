package com.noob.module.admin.base.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.module.admin.base.user.mapper.UserExtendMapper;
import com.noob.module.admin.base.user.model.entity.UserExtend;
import com.noob.module.admin.base.user.service.UserExtendService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author hahabibu
* @description 针对表【user_extend】的数据库操作Service实现
* @createDate 2024-04-28 09:36:34
*/
@Service
public class UserExtendServiceImpl extends ServiceImpl<UserExtendMapper, UserExtend>
    implements UserExtendService {

    @Override
    public boolean initDefaultUserExtend(long uid, Date currentTime,String registerChannel) {
        // 初始化用户扩展信息（user_extend）
        UserExtend userExtend = new UserExtend();
        userExtend.setUserId(uid);
        userExtend.setIsDevelop(UserConstant.IS_DEVELOP_OFF);
        // 初始化用户积分
        userExtend.setScore(UserConstant.INIT_USER_SCORE);
        // 初始化用户等级
        userExtend.setGrade(UserConstant.INIT_USER_GRADE_USER);
        userExtend.setCreateTime(currentTime);
        userExtend.setUpdateTime(currentTime);
        userExtend.setRegisterChannel(registerChannel);

        // 插入用户扩展信息
        boolean insertUserExtendRes = this.save(userExtend);
        return insertUserExtendRes;
    }
}




