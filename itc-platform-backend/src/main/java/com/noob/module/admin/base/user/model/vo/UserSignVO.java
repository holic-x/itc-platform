package com.noob.module.admin.base.user.model.vo;

import com.noob.module.admin.base.user.model.entity.UserSign;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户签到信息视图
 *
 */
@Data
public class UserSignVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 签到用户ID
     */
    private Long uid;

    /**
     * 冗余字段（用户姓名）
     */
    private String uname;


    /**
     * 签到说明
     */
    private String title;

    /**
     * 开始时间
     */
    private Date signInTime;

    /**
     * 签到渠道
     */
    private String signChannel;

    /**
     * 获取积分
     */
    private Integer score;


    /**
     * 包装类转对象
     *
     * @param userSignVO
     * @return
     */
    public static UserSign voToObj(UserSignVO userSignVO) {
        if (userSignVO == null) {
            return null;
        }
        UserSign userSign = new UserSign();
        BeanUtils.copyProperties(userSignVO, userSign);
        return userSign;
    }

    /**
     * 对象转包装类
     *
     * @param userSign
     * @return
     */
    public static UserSignVO objToVo(UserSign userSign) {
        if (userSign == null) {
            return null;
        }
        UserSignVO userSignVO = new UserSignVO();
        BeanUtils.copyProperties(userSign, userSignVO);
        return userSignVO;
    }
}
