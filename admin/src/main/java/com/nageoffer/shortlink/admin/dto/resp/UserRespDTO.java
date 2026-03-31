package com.nageoffer.shortlink.admin.dto.resp;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nageoffer.shortlink.admin.common.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

@Data
public class UserRespDTO {


    private Long id;


    private String username;


    private String realName;

    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;


    private String mail;


}
