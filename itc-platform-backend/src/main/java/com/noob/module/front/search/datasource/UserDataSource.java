package com.noob.module.front.search.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.user.model.dto.UserQueryRequest;
import com.noob.module.admin.user.model.vo.UserVO;
import com.noob.module.admin.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName UserDataSource
 * @Description 用户信息数据源服务（接口实现）
 * @Author holic-x
 * @Date 2024/4/27 11:25
 */
@Service
@Slf4j
public class UserDataSource implements DataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, int pageNum, int pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return userVOPage;
    }
}
