package com.noob.module.front.search.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.exception.ThrowUtils;
import com.noob.module.front.search.datasource.DataSource;
import com.noob.module.front.search.datasource.DataSourceRegistry;
import com.noob.module.front.search.model.dto.SearchRequest;
import com.noob.module.front.search.model.enums.SearchTypeEnum;
import com.noob.module.front.search.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName SearchFacade
 * @Description 聚合搜索门面（门面模式构建）
 * @Author holic-x
 * @Date 2024/4/27 11:27
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(@RequestBody SearchRequest searchRequest) {
        String type = searchRequest.getSearchType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        String searchText = searchRequest.getSearchText();
        // 搜索出所有数据
        if (searchTypeEnum == null) {
            // 指定type为空则默认搜索所有数据（此处暂不作处理，实现可参考SearchController）
        } else {
            SearchVO searchVO = new SearchVO();
            // 1.从数据源注册器中获取到对应的数据源信息（注册模式）
            DataSource dataSource = dataSourceRegistry.getDataSourceByType(type);
            // 2.根据数据源调用适配器方法获取相应的分页数据（适配器模式）
            Page page = dataSource.doSearch(searchText, searchRequest.getCurrent(), searchRequest.getPageSize());
            // 将查询到的数据信息进行封装并返回
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
        return null;
    }

}
