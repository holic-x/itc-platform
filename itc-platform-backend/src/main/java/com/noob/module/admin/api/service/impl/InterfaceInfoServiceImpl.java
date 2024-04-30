package com.noob.module.admin.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.admin.api.mapper.InterfaceInfoMapper;
import com.noob.module.admin.api.model.dto.InterfaceInfoAddRequest;
import com.noob.module.admin.api.model.entity.InterfaceInfo;
import com.noob.module.admin.api.model.entity.UserInterfaceInfo;
import com.noob.module.admin.api.model.enums.InterfaceInfoEnum;
import com.noob.module.admin.api.service.InterfaceInfoService;
import com.noob.module.admin.api.service.UserInterfaceInfoService;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author hahabibu
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

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
    public long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        // 插入接口信息
        boolean result = save(interfaceInfo);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR, "新增接口失败");
        // 获取新增的接口ID
        long newInterfaceInfoId = interfaceInfo.getId();

        // 绑定接口调用记录：用户新增接口默认为其分配接口调用次数
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setInterfaceInfoId(newInterfaceInfoId);
        userInterfaceInfo.setUserId(loginUser.getId());
        userInterfaceInfo.setTotalNum(0);
        userInterfaceInfo.setLeftNum(100);
        userInterfaceInfo.setIsDelete(0);
        boolean distributionRes = userInterfaceInfoService.save(userInterfaceInfo);
        ThrowUtils.throwIf(!distributionRes,ErrorCode.OPERATION_ERROR, "接口调用次数分配失败，请联系管理员");
        // 返回新增成功的接口ID嘻嘻
        return newInterfaceInfoId;
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

    @Override
    public String invokeInterfaceInfo() {

        // todo 模拟调用访问接口测试
        // 获取当前登录用户的ak和sk，相当于用户自己的这个身份去调用，因为知道是谁刷了这个接口，不会担心它刷接口，会比较安全(client-sdk封装了这个参数)
//        User loginUser = userService.getLoginUser(request);

        /*
        // 取消接口模拟测试，根据请求方式、接口数据来源类型调用访问(用户请求参数、用户请求URL调用访问)
        String sourceType = oldInterfaceInfo.getSourceType();
        String method = oldInterfaceInfo.getMethod();
        ThrowUtils.throwIf(StringUtils.isEmpty(method), ErrorCode.PARAMS_ERROR, "请求接口方式错误");
        ThrowUtils.throwIf(StringUtils.isEmpty(sourceType), ErrorCode.PARAMS_ERROR, "请求接口数据来源类型错误");

        // 获取用户请求参数
        String requestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        String requestParamsBody = null;
        // 空指针处理，用户可以不传入参数
        if (!StringUtils.isEmpty(requestParams)) {
            requestParamsBody = JSONUtil.toJsonStr(requestParams);
        }

        // 根据请求接口不同数据来源调用网关服务（例如外部接口、自研接口等，处理方式不同）
        String res = null;
        if (InterfaceInfoConstant.SOURCE_TYPE_EXTERNAL.equals(sourceType)) {
            // 如果是直接调用第三方接口服务，则直接进行转发即可
        } else if (InterfaceInfoConstant.SOURCE_TYPE_SELF.equals(sourceType)) {
            // 拼接处理URL：调用网关服务
            String url = GATEWAY_IP + oldInterfaceInfo.getUrl();
            log.info("backend 后台请求调用网关服务：" + url);
            if (CommonConstant.METHOD_GET.equals(method)) {
                res = apiClient.getByGateway(requestParamsBody, url); // 通过api-platform-gateway网关调用api-platform-interface接口
            } else if (CommonConstant.METHOD_POST.equals(method)) {
                res = apiClient.postByGateway(requestParamsBody, url); // 通过api-platform-gateway网关调用api-platform-interface接口
            } else {
                // TODO 还可扩展SDK其他处理方式
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口请求方式校验错误");
            }
        } else if (InterfaceInfoConstant.SOURCE_TYPE_GENERAL.equals(sourceType)) {
//            String url = GATEWAY_IP + oldInterfaceInfo.getUrl();
//            res = apiClient.invokeInterfaceGeneral(oldInterfaceInfo.getId(),requestParams,url,method);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前请求来源暂未处理，请联系管理员");
        } else {
            // todo 其他数据来源类型接入
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口请求来源类型错误");
        }

         */
        return "";
    }
}




