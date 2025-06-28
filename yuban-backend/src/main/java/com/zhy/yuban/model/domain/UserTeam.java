package com.zhy.yuban.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户-队伍表
 * @author zhy
 * @TableName user_team
 */
@TableName(value ="user_team")
@Data
public class UserTeam implements Serializable {
    private static final long serialVersionUID = 9176956058249669651L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 队员id
     */
    private Long userId;

    /**
     * 加入时间（数据插入时间）
     */
    private Date joinTime;

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
    @TableLogic
    private Integer isDelete;
}