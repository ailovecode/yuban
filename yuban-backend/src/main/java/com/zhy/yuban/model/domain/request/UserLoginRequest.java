package com.zhy.yuban.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2024/10/22 18:06
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -4780927654973334115L;

    private String userAccount;
    private String userPassword;
}
