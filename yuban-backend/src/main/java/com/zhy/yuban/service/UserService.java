package com.zhy.yuban.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.yuban.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author DELL
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-10-21 08:58:28
*/
public interface UserService extends IService<User> {
    /**
     * 注册服务
     *
     * @param userAccount  账户
     * @param userPassword  密码
     * @param checkUserPassword 校验密码
     * @return 返回信用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkUserPassword,String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount 账户
     * @param userPassword 密码
     * @return 返回脱敏用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param originalUser 原始用户信息
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User originalUser);

    /**
     * 用户登出
     * @param request
     * @return
     */
    Integer logout(HttpServletRequest request);

    /**
     * 根据标签查询用户
     * @param tagList
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);

    /**
     * 判断当前用户是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 权限验证（判断是否是管理员或者本人的操作，用于修改个人信息校验）
     * @param request
     * @return
     */
    boolean permissionVerification(User user, HttpServletRequest request);

    /**
     *  修改用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 主页推荐用户查询
     * @param pageSize
     * @param pageNum
     * @param request
     * @return
     */
    Page<User> commendUser(int pageSize, int pageNum, HttpServletRequest request);
}
