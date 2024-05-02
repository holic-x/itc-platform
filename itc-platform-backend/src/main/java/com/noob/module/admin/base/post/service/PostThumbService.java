package com.noob.module.admin.base.post.service;

import com.noob.module.admin.base.post.model.entity.PostThumb;
import com.noob.module.admin.base.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;

/**
 * 帖子点赞服务
 *
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     *
     * @param postId
     * @return
     */
    int doPostThumb(long postId );

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);
}
