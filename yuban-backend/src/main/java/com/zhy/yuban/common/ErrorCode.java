package com.zhy.yuban.common;

/**
 * 错误码信息
 *
 * @version 1.0
 * @Author zhy
 * @Date 2024/11/8 15:28
 */
public enum ErrorCode {
    /**
     * 请求类型
     */
    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    SYSTEM_ERROR(50000,"系统内部异常","");

    /**
     * 错误码
     */
    private final int code;
    /**
     * 错误信息
     */
    private final String message;
    /**
     * 错误描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
