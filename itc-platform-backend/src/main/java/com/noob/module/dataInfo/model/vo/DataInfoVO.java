package com.noob.module.dataInfo.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.noob.module.base.model.entity.User;
import com.noob.module.base.model.vo.UserVO;
import com.noob.module.dataInfo.model.entity.DataInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 模板视图
 *
 */
@Data
public class DataInfoVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 数据内容
     */
    private String dataContent;

    /**
     * 创建者
     */
    private UserVO creater;

    /**
     * 冗余字段（创建者姓名）
     */
    private String createrName;

    /**
     * 修改者
     */
    private UserVO updater;

    /**
     * 数据状态
     */
    private Integer status;

    /**
     * 删除状态
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param dataInfoVO
     * @return
     */
    public static DataInfo voToObj(DataInfoVO dataInfoVO) {
        if (dataInfoVO == null) {
            return null;
        }
        DataInfo dataInfo = new DataInfo();
        BeanUtils.copyProperties(dataInfoVO, dataInfo);
        return dataInfo;
    }

    /**
     * 对象转包装类
     *
     * @param dataInfo
     * @return
     */
    public static DataInfoVO objToVo(DataInfo dataInfo) {
        if (dataInfo == null) {
            return null;
        }
        DataInfoVO dataInfoVO = new DataInfoVO();
        BeanUtils.copyProperties(dataInfo, dataInfoVO);
        return dataInfoVO;
    }
}
