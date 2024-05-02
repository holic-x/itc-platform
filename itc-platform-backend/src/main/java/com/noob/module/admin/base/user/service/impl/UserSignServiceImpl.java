package com.noob.module.admin.base.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.admin.base.user.model.dto.UserSignQueryRequest;
import com.noob.module.admin.base.user.model.entity.UserSign;
import com.noob.module.admin.base.user.model.vo.UserSignVO;
import com.noob.module.admin.base.user.service.UserSignService;
import com.noob.module.admin.base.user.mapper.UserSignMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author hahabibu
* @description 针对表【user_sign】的数据库操作Service实现
* @createDate 2024-05-02 15:30:59
*/
@Service
public class UserSignServiceImpl extends ServiceImpl<UserSignMapper, UserSign>
    implements UserSignService{

    @Resource
    private UserSignMapper userSignMapper;


    @Override
    public UserSignVO getVOById(long id) {
        return userSignMapper.getVOById(id);
    }

    @Override
    public Page<UserSignVO> getVOByPage(UserSignQueryRequest userSignQueryRequest) {
        long current = userSignQueryRequest.getCurrent();
        long size = userSignQueryRequest.getPageSize();
        Page<T> page = new Page<>(current, size);
        Page<UserSignVO> userSignVOPage = userSignMapper.getVOByPage(userSignQueryRequest,page);
        return userSignVOPage;
    }

    @Override
    public UserSignVO validCurrentUserSign() {
        UserSignVO  userSignVO = userSignMapper.getTodaySinInByUid(ShiroUtil.getCurrentUserId());
        return userSignVO;
    }
}




