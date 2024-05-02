package com.noob.module.admin.base.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.base.user.model.dto.UserSignQueryRequest;
import com.noob.module.admin.base.user.model.entity.UserSign;
import com.noob.module.admin.base.user.model.vo.UserSignVO;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
* @author hahabibu
* @description 针对表【user_sign】的数据库操作Mapper
* @createDate 2024-05-02 15:30:59
* @Entity generator.domain.UserSign
*/
public interface UserSignMapper extends BaseMapper<UserSign> {

    // 根据ID查找数据
    UserSignVO getVOById(@Param("id") long id);


    // 分页查找数据
    Page<UserSignVO> getVOByPage(@Param("params") UserSignQueryRequest dataInfoQueryRequest, Page<T> page);


    // 获取指定用户当天签到记录
    UserSignVO getTodaySinInByUid(@Param("uid")long uid);

}




