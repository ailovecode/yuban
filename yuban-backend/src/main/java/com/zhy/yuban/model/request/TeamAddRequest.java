package com.zhy.yuban.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/26 22:09
 */
@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = 1392322038759896289L;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队长id
     */
    private Long captainId;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private Integer maxNumber;

    /**
     * 入队密码
     */
    private String teamPassword;
}
