package com.noob.framework.common;

/**
 * 自定义错误码（结合系统和业务需求自定义错误码，根据错误码信息可快速定位系统问题）
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    USER_STATUS_FORBID_ERROR(40102, "用户账号状态异常"),
    USER_EMAIL_REPEAT_ERROR(40103, "用户邮箱账号重复（已被注册）"),
    VALID_CODE_ERROR(40104, "验证码验证错误"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    TOO_MANY_REQUEST(42900,"请求过于频繁"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
