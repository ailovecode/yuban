package com.zhy.yuban.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.yuban.common.ErrorCode;
import com.zhy.yuban.exception.BusinessException;
import com.zhy.yuban.mapper.UserMapper;
import com.zhy.yuban.model.domain.User;
import com.zhy.yuban.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zhy.yuban.constant.UserConstant.ADMIN_ROLE;
import static com.zhy.yuban.constant.UserConstant.USER_LOGIN_STATUS;

/**
* @author ZHY
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-10-21 08:58:28
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    private static final String SALT = "zhy";
    private static final Integer MAX = 100000;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkUserPassword, String planetCode) {
        // 判断用户名、密码、确认密码是否为空
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkUserPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"注册信息为空");
        }
        // 判断用户名长度是否小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过短");
        }
        // 判断星球编号长度是否大于5
        if(planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }

        // 判断用户名长度是否大于15
        if (userAccount.length() > 15) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过长");
        }
        // 判断密码长度是否小于8
        if(userPassword.length() < 8 || checkUserPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }

        // 判断用户名是否包含特殊字符
        String pattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if(matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名不能有特殊字符");
        }
        // 判断两次密码是否相同
        if(!userPassword.equals(checkUserPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不同");
        }

        // 账户不能重复
        QueryWrapper<User> user = new QueryWrapper<>();
        user.eq("userAccount", userAccount);
        if(userMapper.selectOne(user) != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该用户已经存在");
        }

        // 星球编号不能重复
        user = new QueryWrapper<>();
        user.eq("planetCode", planetCode);
        if(userMapper.selectOne(user) != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该星球编号已经存在");
        }

        // 对密码进行加密
        String md5UserPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user1 = new User();
        user1.setUserAccount(userAccount);
        user1.setUserPassword(md5UserPassword);
        user1.setPlanetCode(planetCode);
        boolean save = this.save(user1);
        if(!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }

        return user1.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验账户和密码
        if(StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"登录信息为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过短");
        }
        if(userAccount.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过长");
        }
        if(userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        String pattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if(matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名不能包含特殊字符");
        }
        // 2. 查询用户信息 （这里涉及到逻辑上删除，使用MybatisPlus框架来处理）
        String md5UserPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> userLoginMessage = new QueryWrapper<>();
        userLoginMessage.eq("userAccount", userAccount);
        userLoginMessage.eq("userPassword", md5UserPassword);
        User user = userMapper.selectOne(userLoginMessage);
        if(user == null) {
            log.info("The userAccount and password do not match!");
            throw new BusinessException(ErrorCode.NULL_ERROR,"不存在此用户");
        }
        // 3. 用户信息的脱敏处理
        User safetyUser = getSafetyUser(user);
        // 4. 存储用户信息
        request.getSession().setAttribute(USER_LOGIN_STATUS,safetyUser);
        // 5. 返回脱敏用户信息
        return safetyUser;
    }

    @Override
    public User getSafetyUser(User originalUser) {
        if(originalUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户信息为空");
        }
        User safetyUser =  new User();
        safetyUser.setId(originalUser.getId());
        safetyUser.setUsername(originalUser.getUsername());
        safetyUser.setUserAccount(originalUser.getUserAccount());
        safetyUser.setAvatarUrl(originalUser.getAvatarUrl());
        safetyUser.setGender(originalUser.getGender());
        safetyUser.setPhone(originalUser.getPhone());
        safetyUser.setEmail(originalUser.getEmail());
        safetyUser.setPlanetCode(originalUser.getPlanetCode());
        safetyUser.setUserStatus(originalUser.getUserStatus());
        safetyUser.setCreateTime(originalUser.getCreateTime());
        safetyUser.setUserRole(originalUser.getUserRole());
        safetyUser.setTags(originalUser.getTags());
        safetyUser.setProfile(originalUser.getProfile());
        return safetyUser;
    }

    @Override
    public Integer logout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }

    @Override
    public List<User> searchUserByTags(List<String> tagList) {
        // 1、 判断是否为空
        if(CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 内存查询
        // 1。 查询所用用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        // 2. 使用 filter 保留满足条件的数据
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            // 将标签信息 反序列化 称对象来比较
            Gson gson = new Gson();
            Set<String> tagNameSet =  gson.fromJson(tagStr, new TypeToken<Set<String>>(){}.getType());
            // 减少圈复杂度
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            for(String tagName : tagList) {
                if(!tagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User) attribute;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean permissionVerification(User user, HttpServletRequest request) {
        if(request == null) {
            return false;
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User loginUser = (User) attribute;
        if(Objects.equals(loginUser.getId(), user.getId()) || loginUser.getUserRole() == ADMIN_ROLE) {
            return true;
        }
        return false;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        // 1. 判断用户是否存在
        long userId = user.getId();
        if(userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User oldUser = userMapper.selectById(userId);
        if(oldUser == null) {
            throw new BusinessException((ErrorCode.NULL_ERROR));
        }
        // 2. 更信息用户信息
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATUS);
        if(attribute == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return (User) attribute;
    }

    @Override
    public Page<User> commendUser(int pageSize, int pageNum, HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);

        // 为每个用户设计唯一的key
        String redisKey = String.format("yuban:user:commend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        // 判断当前缓存中是否存在目前用户对应的数据
        if(userPage != null) {
            return userPage;
        }

        // 没有缓存从数据库中查询数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = this.page(new Page<>((long) (pageNum - 1) * pageSize, pageSize), queryWrapper);

        // 将查询的数据写入缓存中
        try {
            // 缓存有效时间为1小时
            valueOperations.set(redisKey, userPage, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.info("redis set key error: ", e);
        }

        return userPage;
    }

    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagList) {
        // 1、 判断是否为空
        if(CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // long startTime = System.currentTimeMillis();
        // SQL 查询方式
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        for(String tagName : tagList) {
            userQueryWrapper = userQueryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(userQueryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




