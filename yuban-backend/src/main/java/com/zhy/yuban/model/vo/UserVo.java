package com.zhy.yuban.model.vo;
import lombok.Data;

import java.io.Serializable;
/**
 * @version 1.0
 * @Author zhy
 * @Date 2025/6/27 9:57
 */
@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = -7850445578052943128L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户标签
     */
    private String tags;
}
