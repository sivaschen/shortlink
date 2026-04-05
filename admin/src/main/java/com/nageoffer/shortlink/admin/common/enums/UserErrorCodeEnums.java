package com.nageoffer.shortlink.admin.common.enums;

import com.nageoffer.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnums implements IErrorCode {


    USER_TOKEN_FAIL("A000200", "用户token验证失败"),
    USER_NAME_EXIST("B000201", "用户名已存在"),
    USER_NULL("B000200", "用户记录不存在"),
    USER_SAVE_ERROR("B000203", "用户新增失败");

    private final String code;

    private final String message;

    UserErrorCodeEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
