package com.noob.module.admin.search.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.common.*;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.search.model.dto.BossJobQueryRequest;
import com.noob.module.admin.search.model.entity.BossJob;
import com.noob.module.admin.search.model.vo.BossJobVO;
import com.noob.module.admin.search.service.BossJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据接口
 */
//@Api(tags = {"admin","BossJobController"})
@RestController
@RequestMapping("/admin/bossJob")
@Slf4j
public class BossJobController {

    @Resource
    private BossJobService bossJobService;


    // region 增删改查

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @RequiresRoles(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteBossJob(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        BossJob oldBossJob = bossJobService.getById(id);
        ThrowUtils.throwIf(oldBossJob == null, ErrorCode.NOT_FOUND_ERROR);
        // 删除数据
        boolean b = bossJobService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<BossJobVO> getBossJobVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(bossJobService.getVOById(id));
    }

    /**
     * 分页获取列表（自定义SQL处理）
     *
     * @param bossJobQueryRequest
     * @return
     */
    @PostMapping("/listBossJobVOByPage")
    public BaseResponse<Page<BossJobVO>> listBossJobVOByPage(@RequestBody BossJobQueryRequest bossJobQueryRequest) {
        // 获取分页信息
        return ResultUtils.success(bossJobService.getVOByPage(bossJobQueryRequest));
    }
    // endregion

    // --------------- 批量操作定义 ---------------
    /**
     * 批量删除数据
     *
     * @param batchDeleteRequest
     * @return
     */
    @PostMapping("/batchDeleteBossJob")
    public BaseResponse<Boolean> batchDeleteBossJob(@RequestBody BatchDeleteRequest batchDeleteRequest) {
        if (batchDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 执行批量操作
        List<Long> idList = batchDeleteRequest.getIdList();
        if(idList == null || idList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"指定操作列表不能为空");
        }
        // 批量删除
        boolean b = bossJobService.removeBatchByIds(idList);
        return ResultUtils.success(b);
    }

}
