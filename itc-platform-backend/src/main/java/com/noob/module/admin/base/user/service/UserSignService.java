package com.noob.module.admin.base.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.base.user.model.dto.UserSignQueryRequest;
import com.noob.module.admin.base.user.model.entity.UserSign;
import com.noob.module.admin.base.user.model.vo.UserSignVO;


/**
* @author hahabibu
* @description 针对表【user_sign】的数据库操作Service
* @createDate 2024-05-02 15:30:59
*/
public interface UserSignService extends IService<UserSign> {

    /**
     * 根据ID获取数据封装
     *
     * @param id
     * @return
     */
    UserSignVO getVOById(long id);

    /**
     * 分页获取数据封装(SQL处理)
     *
     * @param userSignQueryRequest
     * @return
     */
    Page<UserSignVO> getVOByPage(UserSignQueryRequest userSignQueryRequest);


    /**
     * 校验当前用户签到状态
     *
     * @return
     */
    UserSignVO validCurrentUserSign();

}
