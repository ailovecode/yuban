package com.zhy.yuban.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.yuban.common.BaseResponse;
import com.zhy.yuban.common.ErrorCode;
import com.zhy.yuban.common.ResultUtil;
import com.zhy.yuban.exception.BusinessException;
import com.zhy.yuban.mapper.UserMapper;
import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.model.domain.request.UserLoginRequest;
import com.zhy.yuban.model.domain.request.UserRegisterRequest;
import com.zhy.yuban.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zhy.yuban.constant.UserConstant.ADMIN_ROLE;
import static com.zhy.yuban.constant.UserConstant.USER_LOGIN_STATUS;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2024/10/22 17:38
 */
@RestController
@Api(tags = "首页模块")
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173/", allowCredentials = "true")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "注册信息为空");
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkUserPassword = userRegisterRequest.getCheckUserPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkUserPassword, planetCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "注册信息参数为空");
        }

        long result = userService.userRegister(userAccount, userPassword, checkUserPassword, planetCode);

        return ResultUtil.success(result);
    }

    @GetMapping("/currentUser")
    public BaseResponse<User> userCurrent(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "当前没有用户信息");
        }
        User user = userService.getById(currentUser.getId());
        User result = userService.getSafetyUser(user);
        return ResultUtil.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "登录信息为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "登录信息参数为空");
        }

        User result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(result);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request != null) {
            userService.logout(request);
            return ResultUtil.success(1);
        }
        return ResultUtil.success(0);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> userList(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(username)) {
            wrapper.like("username", username);
        }
        List<User> userList = userService.list(wrapper);
        List<User> result = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtil.success(result);
    }

    /**
     * 根据标签查找用户
     *
     * @param tagList
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> result = userService.searchUserByTags(tagList);
        return ResultUtil.success(result);
    }

    /**
     * 主页推荐用户信息
     * @param request
     * @return
     */
    @GetMapping("/commend")
    public BaseResponse<Page<User>> commendUser(int pageSize, int pageNum, HttpServletRequest request) {

        Page<User> userPage = userService.commendUser(pageSize, pageNum, request);

        return ResultUtil.success(userPage);
    }

    /**
     * 删除用户信息
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> useDelete(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID错误");
        }
        boolean result = userService.removeById(id);
        return ResultUtil.success(result);
    }

    /**
     * 更新个人信息，或者管理员更新他人信息
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {

        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息为空");
        }

        if (!userService.permissionVerification(user, request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有权限修改或非本人操作");
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        int result = userService.updateUser(user, loginUser);
        return ResultUtil.success(result);
    }
}
