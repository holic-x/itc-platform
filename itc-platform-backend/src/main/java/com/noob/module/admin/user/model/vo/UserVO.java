package com.noob.module.admin.user.model.vo;

import java.io.Serializable;
import java.util.Date;

import com.noob.module.admin.user.model.entity.UserExtend;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 用户扩展信息
     */
//    private UserExtend userExtend;

    /**
     * 开发者accessKey
     */
    private String accessKey;

    /**
     * 开发者secretKey
     */
    private String secretKey;

    /**
     * 是否开启开发者模式（申请ak/sk）
     */
    private Integer isDevelop;

    /**
     * 用户网站积分
     */
    private Integer score;

    /**
     * 用户VIP等级（USER、VIP、SVIP等）
     */
    private String grade;

    /**
     * 用户注册渠道（后台管理员添加、网站注册、其他接入方式等）
     */
    private String registerChannel;


    private static final long serialVersionUID = 1L;
}