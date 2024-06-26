package com.noob.module.admin.base.post.controller;

import com.noob.framework.common.BaseResponse;
import com.noob.framework.common.ErrorCode;
import com.noob.framework.common.ResultUtils;
import com.noob.framework.realm.ShiroUtil;
import com.noob.module.admin.base.post.model.dto.postthumb.PostThumbAddRequest;
import com.noob.module.admin.base.user.model.entity.User;
import com.noob.module.admin.base.post.service.PostThumbService;
import com.noob.module.admin.base.user.model.vo.LoginUserVO;
import com.noob.module.admin.base.user.service.UserService;
import com.noob.framework.exception.BusinessException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子点赞接口
 *
 */
@RestController
@RequestMapping("/admin/post_thumb")
@Slf4j
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final LoginUserVO currentUser = ShiroUtil.getCurrentUser();
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId);
        return ResultUtils.success(result);
    }

}
