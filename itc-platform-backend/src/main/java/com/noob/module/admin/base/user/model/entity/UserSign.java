package com.noob.module.admin.base.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName user_sign
 */
@TableName(value ="user_sign")
@Data
public class UserSign implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 签到用户ID
     */
    private Long uid;

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
    private String signInChannel;

    /**
     * 获取积分
     */
    private Integer score;

    /**
     * 是否删除
     */
//    @TableLogic  用户签到记录取消逻辑删除，可硬删除
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
        UserSign other = (UserSign) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUid() == null ? other.getUid() == null : this.getUid().equals(other.getUid()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getSignInTime() == null ? other.getSignInTime() == null : this.getSignInTime().equals(other.getSignInTime()))
            && (this.getSignInChannel() == null ? other.getSignInChannel() == null : this.getSignInChannel().equals(other.getSignInChannel()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSignInTime() == null) ? 0 : getSignInTime().hashCode());
        result = prime * result + ((getSignInChannel() == null) ? 0 : getSignInChannel().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
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
        sb.append(", uid=").append(uid);
        sb.append(", title=").append(title);
        sb.append(", signInTime=").append(signInTime);
        sb.append(", signChannel=").append(signInChannel);
        sb.append(", score=").append(score);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}