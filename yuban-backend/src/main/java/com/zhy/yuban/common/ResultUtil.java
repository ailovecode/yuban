package com.zhy.yuban.common;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2024/11/8 15:11
 */
public class ResultUtil {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0,data,"ok");
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse(code,null,message,description);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode,String message,String description) {
        return new BaseResponse (errorCode.getCode(),null,message,description);
    }
}
