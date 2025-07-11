package com.zhy.yuban.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @version 1.0
 * @Author zhy
 * @Date 2024/11/8 15:06
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data,String message) {
        this(code, data, message ,"");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode,T data,String message,String description) {
        this(errorCode.getCode(), null, message, description);
    }

    public BaseResponse(ErrorCode errorCode,T data,String message) {
        this(errorCode.getCode(), null, message, errorCode.getDescription());
    }
}
