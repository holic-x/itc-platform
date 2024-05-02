package com.noob.module.admin.search.model.vo;

import cn.hutool.json.JSONUtil;
import com.noob.module.admin.search.model.entity.FetchPost;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName FetchPostVO
 * @Description TODO
 * @Author holic-x
 * @Date 2024/4/27 17:13
 */
@Data
public class FetchPostVO  implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签列表（json 转化为 数组）
     */
    private String tags;

    /**
     * 标签列表（json 转化为 数组）
     */
    private List<String> tagList;

    /**
     * 阅读数
     */
    private Integer viewNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 评论数
     */
    private Integer commentNum;

    /**
     * 创建用户 id
     */
    private String userId;

    /**
     * 创建者名称
     */
    private String userName;

    /**
     * 创建者信息
     */
    private String userInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 文章状态（是否同步）
     */
    private Integer status;


    private static final long serialVersionUID = 1L;


    /**
     * 包装类转对象
     *
     * @param fetchPostVO
     * @return
     */
    public static FetchPost voToObj(FetchPostVO fetchPostVO) {
        if (fetchPostVO == null) {
            return null;
        }
        FetchPost fetchPost = new FetchPost();
        BeanUtils.copyProperties(fetchPostVO, fetchPost);
        List<String> tagList = fetchPostVO.getTagList();
        fetchPost.setTags(JSONUtil.toJsonStr(tagList));
        return fetchPost;
    }


    /**
     * 对象转包装类
     *
     * @param fetchPost
     * @return
     */
    public static FetchPostVO objToVo(FetchPost fetchPost) {
        if (fetchPost == null) {
            return null;
        }
        FetchPostVO fetchPostVO = new FetchPostVO();
        BeanUtils.copyProperties(fetchPost, fetchPostVO);
        fetchPostVO.setTagList(JSONUtil.toList(fetchPost.getTags(), String.class));
        return fetchPostVO;
    }
}