package com.noob.module.admin.base.post.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.base.post.model.entity.Post;
import com.noob.module.admin.base.post.model.entity.PostFavour;
import com.noob.module.admin.base.user.model.entity.User;

/**
 * 帖子收藏服务
 *
 */
public interface PostFavourService extends IService<PostFavour> {

    /**
     * 帖子收藏
     *
     * @param postId
     * @return
     */
    int doPostFavour(long postId);

    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
                                    long favourUserId);

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostFavourInner(long userId, long postId);
}
