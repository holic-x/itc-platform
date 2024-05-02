package com.noob.module.admin.api.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 *
 */
public enum InterfaceInfoEnum {

    DRAFT("草稿", 1),
    WAIT_AUDIT("待审核", 2),
    AUDIT_PASS("审核通过", 3),
    AUDIT_REJECT("审核失败", 4),
    ONLINE("上线", 5),
    OFFLINE("禁用", 0);

    private final String text;

    private final int value;

    InterfaceInfoEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 获取值列表
     *
     * @return
     */
//    public static List<String> getValues() {
//        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
//    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static InterfaceInfoEnum getEnumByValue(int value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (InterfaceInfoEnum anEnum : InterfaceInfoEnum.values()) {
            if (anEnum.value==value) {
                return anEnum;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
