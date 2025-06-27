package com.zhy.yuban.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/27 9:57
 */
@Data
public class TeamUserVo implements Serializable {

    private static final long serialVersionUID = -6999340265727150439L;

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

    /**
     * 创建时间（数据插入时间）
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 队员信息列表
     */
    private List<UserVo> users;
}
