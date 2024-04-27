package com.noob.module.front.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.exception.BusinessException;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.front.search.datasource.DataSource;
import com.noob.module.front.search.datasource.PictureDataSource;
import com.noob.module.front.search.datasource.PostDataSource;
import com.noob.module.front.search.datasource.UserDataSource;
import com.noob.module.front.search.manager.SearchFacade;
import com.noob.module.front.search.model.dto.SearchRequest;
import com.noob.module.front.search.model.enums.SearchTypeEnum;
import com.noob.module.front.search.model.vo.SearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SearchOptimizeController
 * @Description 聚合搜索接口（优化：引入设计模式构建）
 * @Author holic-x
 * @Date 2024/4/27 11:22
 */
@RestController
@RequestMapping("/searchOptimize")
@Slf4j
public class SearchOptimizeController {

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private SearchFacade searchFacade;

    /**
     * V4.1 基于门面模式改造，即将原有controller层中的业务逻辑处理统一设定一个SearchFacade做处理
     * controller不关心任何业务处理逻辑，而是通过一个入口指定入参和出参，所有处理交由SearchFacade
     *
     * @param searchRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "检索所有(基于门面模式改造优化)")
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAllByCondFacade(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        // 调用门面将查询到的数据信息进行封装并返回
        return ResultUtils.success(searchFacade.searchAll(searchRequest, request));
    }

    /**
     * V4.2 基于设计模式改造：适配器模式改造介入不同数据源、注册器模式替代传统if...else...
     *
     * @param searchRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "检索所有(基于多种设计模式改造优化)")
    @PostMapping("/allByCondAdaptor")
    public BaseResponse<SearchVO> searchAllByCondAdaptor(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        String type = searchRequest.getSearchType();
        // 检验传入指定类型为空字符串则抛出异常
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        // 校验指定type不在指定的字符串范围内则抛出异常或者默认搜索出所有数据（结合业务场景处理）
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
//        ThrowUtils.throwIf(searchTypeEnum==null,ErrorCode.PARAMS_ERROR);
        if (searchTypeEnum == null) {
            // 指定type为空则默认搜索所有数据（此处暂不作处理，实现可参考SearchController）
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"指定搜索类型错误");
        } else {
            SearchVO searchVO = new SearchVO();
            // 封装枚举类型类别,根据类别获取到对应的数据源
            Map<String, DataSource> dataSourceMap = new HashMap();
            dataSourceMap.put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
            dataSourceMap.put(SearchTypeEnum.USER.getValue(), userDataSource);
            dataSourceMap.put(SearchTypeEnum.POST.getValue(), postDataSource);
            DataSource dataSource = dataSourceMap.get(type);
            // 根据数据源调用适配器方法获取相应的分页数据
            Page page = dataSource.doSearch(searchText, searchRequest.getCurrent(), searchRequest.getPageSize());
            // 将查询到的数据信息进行封装并返回
            searchVO.setDataList(page.getRecords());
            return ResultUtils.success(searchVO);
        }
//        return null;
    }
}
