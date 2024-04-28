package com.noob.module.admin.interfaceInfo.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 接口信息状态枚举
 *
 */
public enum InterfaceInfoEnum {

    OFFLINE("禁用", 0),
    ONLINE("上线", 1);

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
