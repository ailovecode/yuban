package com.zhy.yuban.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 队伍表
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team {
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
     * 
     */
    private Date updateTime;

    /**
     * 是否删除（逻辑删除 0 1）
     */
    private Integer isDelete;
}