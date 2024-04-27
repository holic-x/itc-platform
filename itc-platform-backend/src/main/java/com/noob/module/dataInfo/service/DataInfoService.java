package com.noob.module.dataInfo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.dataInfo.model.entity.DataInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.dataInfo.model.dto.DataInfoQueryRequest;
import com.noob.module.dataInfo.model.entity.DataInfo;
import com.noob.module.dataInfo.model.vo.DataInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author HUAWEI
* @description 针对表【data_info】的数据库操作Service
* @createDate 2024-04-22 09:03:07
*/
public interface DataInfoService extends IService<DataInfo> {

    /**
     * 校验
     *
     * @param dataInfo
     * @param add
     */
    void validDataInfo(DataInfo dataInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param dataInfoQueryRequest
     * @return
     */
    QueryWrapper<DataInfo> getQueryWrapper(DataInfoQueryRequest dataInfoQueryRequest);

    /**
     * 获取封装
     *
     * @param dataInfo
     * @param request
     * @return
     */
    DataInfoVO getDataInfoVO(DataInfo dataInfo, HttpServletRequest request);

    /**
     * 分页获取数据封装
     *
     * @param dataInfoPage
     * @param request
     * @return
     */
    Page<DataInfoVO> getDataInfoVOPage(Page<DataInfo> dataInfoPage, HttpServletRequest request);


    /**
     * 分页获取数据封装(SQL处理)
     *
     * @param dataInfoQueryRequest
     * @return
     */
    Page<DataInfoVO> getVOByPage(DataInfoQueryRequest dataInfoQueryRequest);

}
