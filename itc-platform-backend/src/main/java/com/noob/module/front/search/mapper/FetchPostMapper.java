package com.noob.module.front.search.mapper;

import com.noob.module.admin.base.post.model.entity.Post;
import com.noob.module.admin.search.model.entity.FetchPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author hahabibu
* @description 针对表【fetch_post(帖子)】的数据库操作Mapper
* @createDate 2024-04-27 15:22:45
* @Entity com.noob.module.admin.search.model.entity.FetchPost
*/
public interface FetchPostMapper extends BaseMapper<FetchPost> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<FetchPost> listPostWithDelete(Date minUpdateTime);

}




