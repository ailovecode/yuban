
package com.zhy.yuban.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签表
 * @TableName tag
 * @author zhy
 */
@TableName(value ="tag")
@Data
public class Tag implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    @TableField(value = "tagName")
    private String tagName;

    /**
     * 用户 id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 父标签 id
     */
    @TableField(value = "parentId")
    private Long parentId;

    /**
     * 是否为父标签
     */
    @TableField(value = "isParent")
    private Integer isParent;

    /**
     * 创建时间（数据插入时间）
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     *
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除（逻辑删除 0 1）
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
