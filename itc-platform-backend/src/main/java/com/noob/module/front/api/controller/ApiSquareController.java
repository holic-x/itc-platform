package com.noob.module.front.api.controller;

import com.noob.framework.annotation.AuthCheck;
import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.module.admin.base.user.constant.UserConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.module.admin.api.mapper.UserInterfaceInfoCallMapper;
import com.noob.module.admin.api.model.entity.InterfaceInfo;
import com.noob.module.front.api.model.dto.InterfaceInfoStatisticQueryRequest;
import com.noob.module.front.api.model.vo.InterfaceInfoStatisticVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
