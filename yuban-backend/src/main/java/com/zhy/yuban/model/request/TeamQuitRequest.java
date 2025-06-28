package com.zhy.yuban.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/27 20:51
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -5667568869066733649L;

    /**
     * 队伍 id
     */
    private String teamId;
}
