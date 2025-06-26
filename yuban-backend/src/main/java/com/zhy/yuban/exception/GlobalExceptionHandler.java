package com.zhy.yuban.exception;

import com.zhy.yuban.common.BaseResponse;
import com.zhy.yuban.common.ErrorCode;
import com.zhy.yuban.common.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2024/11/8 21:13
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
        log.info(e.getDescription());
        return ResultUtil.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(BusinessException e) {

        return ResultUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),e.getDescription());
    }
}
