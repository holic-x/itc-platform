package com.noob.module.admin.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.api.mapper.InterfaceInfoMapper;
import com.noob.module.admin.api.model.entity.InterfaceInfo;
import com.noob.module.admin.api.model.enums.InterfaceInfoEnum;
import com.noob.module.admin.api.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author hahabibu
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {


        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
//        if (add) {
//            if (StringUtils.isAnyBlank()) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR);
//            }
//        }

        // 获取接口信息对象的名称
        String name = interfaceInfo.getName();

        // 如果是添加操作,所有参数必须非空,否则抛出参数错误的异常
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // 如果接口名称不为空且长度大于50,抛出参数错误的异常,错误信息为"名称过长"
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

    @Override
    public boolean handleInterfaceInfoStatus(long interfaceInfoId, int status) {
        // 1.判断是否存在
        InterfaceInfo oldInterfaceInfo = this.getById(interfaceInfoId);
        ThrowUtils.throwIf(oldInterfaceInfo==null,ErrorCode.NOT_FOUND_ERROR);
        // 2.检查接口状态是否已经发生变更(避免重复操作)
        ThrowUtils.throwIf(oldInterfaceInfo.getStatus() == status,ErrorCode.OPERATION_ERROR,"请勿重复操作");
        // 3.更新接口状态数据
        InterfaceInfo newInterfaceInfo = new InterfaceInfo();
        newInterfaceInfo.setId(interfaceInfoId);
        newInterfaceInfo.setStatus(status);
        return updateById(newInterfaceInfo);
    }
}




