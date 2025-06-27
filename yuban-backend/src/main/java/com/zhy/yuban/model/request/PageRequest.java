package com.zhy.yuban.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/26 21:06
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -1388798299085462232L;
    /**
     * 目前页号
     */
    protected int pageNum = 1;

    /**
     * 每页的数量
     */
    protected int pageSize = 10;
}
