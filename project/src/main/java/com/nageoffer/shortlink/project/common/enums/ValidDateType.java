package com.nageoffer.shortlink.project.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ValidDateType {



    PERMANENT(0),
        CUSTOM(1);

    @Getter
    private final int type;

}
