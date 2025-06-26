package com.zhy.yuban.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2024/10/22 17:58
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 7049702280339494770L;

    private String userAccount;
    private String userPassword;
    private String checkUserPassword;
    private String planetCode;
}
