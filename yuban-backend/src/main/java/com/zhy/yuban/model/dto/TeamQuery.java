package com.zhy.yuban.model.dto;

import com.zhy.yuban.model.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/26 21:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    private static final long serialVersionUID = 1144430067027867496L;
    /**
     * 主键 队伍id
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
     * 队伍剩余坑位
     */
    private Integer remainNumber;
}
