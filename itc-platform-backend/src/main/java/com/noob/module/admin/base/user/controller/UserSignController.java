package com.noob.module.admin.base.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.common.*;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.module.admin.base.user.model.dto.UserSignQueryRequest;
import com.noob.module.admin.base.user.model.entity.UserSign;
import com.noob.module.admin.base.user.model.vo.UserSignVO;
import com.noob.module.admin.base.user.service.UserSignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 用户签到信息管理接口
 */
@RestController
@RequestMapping("/admin/userSign")
@Slf4j
public class UserSignController {

    @Resource
    private UserSignService userSignService;

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @RequiresRoles(UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserSign(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = deleteRequest.getId();
        // 判断是否存在
        UserSign oldUserSign = userSignService.getById(id);
        ThrowUtils.throwIf(oldUserSign == null, ErrorCode.NOT_FOUND_ERROR);
        boolean b = userSignService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserSignVO> getUserSignVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserSignVO userSignVO = userSignService.getVOById(id);
        if (userSignVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userSignVO);
    }

    /**
     * 分页获取列表（自定义SQL处理）
     *
     * @param userSignQueryRequest
     * @return
     */
    @PostMapping("/listUserSignVOByPage")
    public BaseResponse<Page<UserSignVO>> listUserSignVOByPage(@RequestBody UserSignQueryRequest userSignQueryRequest) {
        // 获取分页信息
        return ResultUtils.success(userSignService.getVOByPage(userSignQueryRequest));
    }

    // --------------- 批量操作定义 ---------------
    /**
     * 批量删除数据
     *
     * @param batchDeleteRequest
     * @return
     */
    @PostMapping("/batchDeleteUserSign")
    public BaseResponse<Boolean> batchDeleteUserSign(@RequestBody BatchDeleteRequest batchDeleteRequest) {
        if (batchDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 执行批量操作
        List<Long> idList = batchDeleteRequest.getIdList();
        if(idList == null || idList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"指定操作列表不能为空");
        }
        // 批量删除
        boolean b = userSignService.removeBatchByIds(idList);
        return ResultUtils.success(b);
    }

}
