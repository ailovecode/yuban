package com.zhy.yuban.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍表
 * @TableName team
 * @author zhy
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * 主键 队伍id
     */
    @TableId(type = IdType.AUTO)
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
     * 入队密码
     */
    private String teamPassword;

    /**
     * 创建时间（数据插入时间）
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（逻辑删除 0 1）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}