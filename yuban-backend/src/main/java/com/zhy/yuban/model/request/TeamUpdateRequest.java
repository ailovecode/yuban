package com.zhy.yuban.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/27 15:43
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -1365304473458652676L;
    /**
     * 队伍 id
     */
    private Long teamId;

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
