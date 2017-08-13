package com.dev.redis;

import java.io.Serializable;

public class CommonResult<T> implements Serializable {

    private int status;

    private String msg;

    private T data;

    private CommonResult(int status){
        this.status = status;
    }

    private CommonResult(int status, T data){
        this.status = status;
        this.data = data;
    }

    private CommonResult(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private CommonResult(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public boolean isSuccess(){
        return this.status == ResultCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> CommonResult<T> createBySuccess(){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode());
    }

    public static <T> CommonResult<T> createBySuccessMessage(String msg){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(),msg);
    }

    public static <T> CommonResult<T> createBySuccess(T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(),data);
    }

    public static <T> CommonResult<T> createBySuccess(String msg, T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> CommonResult<T> createByError(){
        return new CommonResult<T>(ResultCode.ERROR.getCode());
    }

    public static <T> CommonResult<T> createByErrorMessage(String msg){
        return new CommonResult<T>(ResultCode.ERROR.getCode(),msg);
    }

    public static <T> CommonResult<T> createByError(T data){
        return new CommonResult<T>(ResultCode.ERROR.getCode(),data);
    }

    public static <T> CommonResult<T> createByError(String msg, T data){
        return new CommonResult<T>(ResultCode.ERROR.getCode(), msg, data);
    }

    public static <T> CommonResult<T> createByErrorCodeMessage(int code, String errorMessage){
        return new CommonResult<T>(code,errorMessage);
    }
}
