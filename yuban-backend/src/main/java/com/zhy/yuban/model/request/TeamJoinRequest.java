package com.zhy.yuban.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/27 16:59
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 3928373734769074179L;

    /**
     * 队伍 id
     */
    private Long teamId;

    /**
     * 输入密码
     */
    private String password;
}

