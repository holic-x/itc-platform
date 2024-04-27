package com.noob.module.base.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户账号状态更新请求
 *
 */
@Data
public class UserStatusUpdateRequest implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 操作类型
     */
    private String operType;

    private static final long serialVersionUID = 1L;
}