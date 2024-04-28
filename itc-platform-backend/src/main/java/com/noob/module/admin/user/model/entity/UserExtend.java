package com.noob.module.admin.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户扩展信息字段定义实体
 * @TableName user_extend
 */
@TableName(value ="user_extend")
@Data
public class UserExtend implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联用户id
     */
    private Long userId;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除（0-否；1-是）
     */
    @TableLogic
    private String isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}