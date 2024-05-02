package com.noob.module.admin.base.user.model.dto;

import com.noob.framework.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSignQueryRequest extends PageRequest implements Serializable {

    /**
     * 签到说明
     */
    private String title;

    /**
     * 签到渠道
     */
    private String signInChannel;

    /**
     * 签到用户信息关键字
     */
    private String uname;



    private static final long serialVersionUID = 1L;
}