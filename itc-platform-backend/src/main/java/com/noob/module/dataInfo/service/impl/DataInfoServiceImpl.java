package com.noob.module.dataInfo.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.constant.CommonConstant;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.framework.utils.SqlUtils;
import com.noob.module.base.model.entity.User;
import com.noob.module.base.model.vo.UserVO;
import com.noob.module.base.service.UserService;
import com.noob.module.dataInfo.model.entity.DataInfo;
import com.noob.module.dataInfo.service.DataInfoService;
import com.noob.module.dataInfo.mapper.DataInfoMapper;
import com.noob.module.dataInfo.model.dto.DataInfoQueryRequest;
import com.noob.module.dataInfo.model.entity.DataInfo;
import com.noob.module.dataInfo.model.vo.DataInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author HUAWEI
* @description 针对表【data_info】的数据库操作Service实现
* @createDate 2024-04-22 09:03:07
*/
@Service
public class DataInfoServiceImpl extends ServiceImpl<DataInfoMapper, DataInfo>
    implements DataInfoService{

    @Resource
    private UserService userService;

    @Resource
    private DataInfoMapper dataInfoMapper;

    @Override
    public void validDataInfo(DataInfo dataInfo, boolean add) {
        if (dataInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String dataName = dataInfo.getDataName();
        String dataContent = dataInfo.getDataContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(dataName, dataContent), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(dataName) && dataName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(dataContent) && dataContent.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param dataInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<DataInfo> getQueryWrapper(DataInfoQueryRequest dataInfoQueryRequest) {
        QueryWrapper<DataInfo> queryWrapper = new QueryWrapper<>();
        if (dataInfoQueryRequest == null) {
            return queryWrapper;
        }
        String dataName = dataInfoQueryRequest.getDataName();
        String dataType = dataInfoQueryRequest.getDataType();
        String dataContent = dataInfoQueryRequest.getDataContent();
        Integer status = dataInfoQueryRequest.getStatus();
        String sortField = dataInfoQueryRequest.getSortField();
        String sortOrder = dataInfoQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(dataName)) {
            queryWrapper.and(qw -> qw.like("dataName", dataName));
        }
        if (StringUtils.isNotBlank(dataType)) {
            queryWrapper.and(qw -> qw.eq("dataType", dataType));
        }
        if (StringUtils.isNotBlank(dataContent)) {
            queryWrapper.and(qw -> qw.like("dataContent", dataContent));
        }
        queryWrapper.eq(status!=null, "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public DataInfoVO getDataInfoVO(DataInfo dataInfo, HttpServletRequest request) {

        DataInfoVO dataInfoVO = DataInfoVO.objToVo(dataInfo);
        // 1. 关联查询创建者、修改者信息
        Long createrId = dataInfo.getCreater();
        User creater = null;
        if (createrId != null && createrId > 0) {
            creater = userService.getById(createrId);
        }
        dataInfoVO.setCreater(userService.getUserVO(creater));

        Long updaterId = dataInfo.getCreater();
        User updater = null;
        if (updaterId != null && createrId > 0) {
            updater = userService.getById(createrId);
        }
        dataInfoVO.setUpdater(userService.getUserVO(updater));

        return dataInfoVO;
    }

    @Override
    public Page<DataInfoVO> getDataInfoVOPage(Page<DataInfo> dataInfoPage, HttpServletRequest request) {
        List<DataInfo> dataInfoList = dataInfoPage.getRecords();
        Page<DataInfoVO> dataInfoVOPage = new Page<>(dataInfoPage.getCurrent(), dataInfoPage.getSize(), dataInfoPage.getTotal());
        if (CollUtil.isEmpty(dataInfoList)) {
            return dataInfoVOPage;
        }

        // 获取所有用户信息列表，根据ID封装成Map集合(避免循环搜索数据库)
        List<User> userList = userService.list();
        List<UserVO> userVOList = userService.getUserVO(userList);
        Map<Long,UserVO> userVOMap = new HashMap<>();
        for (UserVO userVO : userVOList) {
            userVOMap.put(userVO.getId(),userVO);
        }

        // 填充信息
        List<DataInfoVO> dataInfoVOList = dataInfoList.stream().map(dataInfo -> {
            DataInfoVO dataInfoVO = DataInfoVO.objToVo(dataInfo);

            // 设置创建者、修改者信息 todo 方式2：通过mapper直接构建sql语句关联查找
            Long createrId = dataInfo.getCreater();
            dataInfoVO.setCreater( userVOMap.get(createrId));
            Long updaterId = dataInfo.getCreater();
            dataInfoVO.setUpdater( userVOMap.get(updaterId));

            dataInfoVO.setStatus(dataInfo.getStatus());
            return dataInfoVO;
        }).collect(Collectors.toList());
        dataInfoVOPage.setRecords(dataInfoVOList);
        return dataInfoVOPage;
    }

    @Override
    public Page<DataInfoVO> getVOByPage(DataInfoQueryRequest dataInfoQueryRequest) {
        long current = dataInfoQueryRequest.getCurrent();
        long size = dataInfoQueryRequest.getPageSize();
        Page<T> page = new Page<>(current, size);
        Page<DataInfoVO> dataInfoVOPage = dataInfoMapper.getVOByPage(dataInfoQueryRequest,page);
        return dataInfoVOPage;
    }
}




