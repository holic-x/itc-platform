package com.noob.module.admin.interfaceInfo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户调用接口关系
 * @TableName user_interface_info_call
 */
@TableName(value ="user_interface_info_call")
@Data
public class UserInterfaceInfoCall implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 调用接口 id
     */
    private Long interfaceInfoId;

    /**
     * 调用接口名称
     */
    private String interfaceInfoName;

    /**
     * 接口调用状态（0-正常，1-禁用）
     */
    private Integer status;

    /**
     * 接口响应异常信息
     */
    private String errMessage;

    /**
     * 接口调用时间
     */
    private Date callTime;

    /**
     * 接口调用耗时
     */
    private String duration;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UserInterfaceInfoCall other = (UserInterfaceInfoCall) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getInterfaceInfoId() == null ? other.getInterfaceInfoId() == null : this.getInterfaceInfoId().equals(other.getInterfaceInfoId()))
            && (this.getInterfaceInfoName() == null ? other.getInterfaceInfoName() == null : this.getInterfaceInfoName().equals(other.getInterfaceInfoName()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getErrMessage() == null ? other.getErrMessage() == null : this.getErrMessage().equals(other.getErrMessage()))
            && (this.getCallTime() == null ? other.getCallTime() == null : this.getCallTime().equals(other.getCallTime()))
            && (this.getDuration() == null ? other.getDuration() == null : this.getDuration().equals(other.getDuration()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getInterfaceInfoId() == null) ? 0 : getInterfaceInfoId().hashCode());
        result = prime * result + ((getInterfaceInfoName() == null) ? 0 : getInterfaceInfoName().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getErrMessage() == null) ? 0 : getErrMessage().hashCode());
        result = prime * result + ((getCallTime() == null) ? 0 : getCallTime().hashCode());
        result = prime * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", interfaceInfoId=").append(interfaceInfoId);
        sb.append(", interfaceInfoName=").append(interfaceInfoName);
        sb.append(", status=").append(status);
        sb.append(", errMessage=").append(errMessage);
        sb.append(", callTime=").append(callTime);
        sb.append(", duration=").append(duration);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}