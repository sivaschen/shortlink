package com.nageoffer.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("t_user")
public class UserDO {

    private Long id;


    private String username;


    private String password;


    private String realName;


    private String phone;


    private String mail;


    private Long deleteTime;


    private Date createTime;


    private Date updateTime;


    private Integer delFlag;

}
