package com.dev.redis;

/**
 * Created by flnnf on 2017/6/27.
 * 返回消息的常量类
 */
public enum ResultCode {

    SUCCESS(0,"SUCCESS"),

    ERROR(1,"ERROR"),

    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;

    private final String desc;

    ResultCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
