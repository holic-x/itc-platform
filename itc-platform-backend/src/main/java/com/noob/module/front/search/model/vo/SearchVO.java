package com.noob.module.front.search.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.module.admin.base.post.model.vo.PostVO;
import com.noob.module.admin.base.user.model.vo.UserVO;
import com.noob.module.front.search.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SearchVO
 * @Description 检索内容列表
 * @Author holic-x
 * @Date 2024/4/27 10:59
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    private List<?> dataList;

    private long total;


    private static final long serialVersionUID = 1L;

}