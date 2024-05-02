package com.noob.module.front.search.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @ClassName DataSource
 * @Description 数据源接口定义（新接入的数据源必须实现）
 * @Author holic-x
 * @Date 2024/4/27 11:23
 */
public interface DataSource<T> {

    /**
     * 定义方法实现数据源检索
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, int pageNum, int pageSize);
}
