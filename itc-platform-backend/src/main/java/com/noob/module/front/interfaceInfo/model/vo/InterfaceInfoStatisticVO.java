package com.noob.module.front.interfaceInfo.model.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.noob.module.admin.dataInfo.model.entity.DataInfo;
import com.noob.module.admin.user.model.vo.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息统计
 */
@Data
public class InterfaceInfoStatisticVO implements Serializable {

    /**
     * 接口ID
     */
    private String interfaceInfoId;

    /**
     * 接口名称
     */
    private String interfaceInfoName;


    /**
     * 接口头像
     */
    private String interfaceInfoAvatar;

    /**
     * 调用总次数
     */
    private String callTotal;

    /**
     * 调用成功总次数
     */
    private String callSuccessNum;


    /**
     * 调用失败总次数
     */
    private String callFailNum;


    /**
     * 异常状态记录条数
     */
    private String errStatusNum;

}
