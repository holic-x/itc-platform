package com.noob.module.front.interfaceInfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.DeleteRequest;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.constant.CommonConstant;
import com.noob.framework.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.interfaceInfo.mapper.UserInterfaceInfoCallMapper;
import com.noob.module.admin.interfaceInfo.model.dto.InterfaceInfoAddRequest;
import com.noob.module.admin.interfaceInfo.model.dto.InterfaceInfoQueryRequest;
import com.noob.module.admin.interfaceInfo.model.dto.InterfaceInfoUpdateRequest;
import com.noob.module.admin.interfaceInfo.model.entity.InterfaceInfo;
import com.noob.module.admin.interfaceInfo.model.entity.UserInterfaceInfo;
import com.noob.module.admin.interfaceInfo.service.InterfaceInfoService;
import com.noob.module.admin.interfaceInfo.service.UserInterfaceInfoService;
import com.noob.module.admin.user.model.entity.User;
import com.noob.module.admin.user.service.UserService;
import com.noob.module.front.interfaceInfo.model.dto.InterfaceInfoStatisticQueryRequest;
import com.noob.module.front.interfaceInfo.model.vo.InterfaceInfoStatisticVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口广场
 */
@RestController("ApiSquareController")
@RequestMapping("/interfaceInfo")
@Slf4j
public class ApiSquareController {

    @Resource
    private UserInterfaceInfoCallMapper userInterfaceInfoCallMapper;




    /**
     * 分页获取接口统计信息
     *
     * @param interfaceInfoStatisticQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfoStatisticVO>> listInterfaceInfoStatisticsByPage(@RequestBody InterfaceInfoStatisticQueryRequest interfaceInfoStatisticQueryRequest) {
        if (interfaceInfoStatisticQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<InterfaceInfoStatisticVO> interfaceInfoStatisticVOList = userInterfaceInfoCallMapper.listInterfaceInfoStatistic(interfaceInfoStatisticQueryRequest);

        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
//        long current = interfaceInfoStatisticQueryRequest.getCurrent();
//        long size = interfaceInfoStatisticQueryRequest.getPageSize();
//        String description = interfaceInfoQuery.getDescription();
        // 限制爬虫
//        if (size > 50) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        return ResultUtils.success(interfaceInfoStatisticVOList);
    }


}
