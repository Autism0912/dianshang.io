package com.hxmall.shop.common.enume;

/**
 * author:黄龙强
 * time:{2023/12/15}
 * version:1.0
 * function:错误状态码枚举
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(7001,"系统未知异常"),
    VALID_EXCEPTION(7002,"参数格式校验异常");

    private int code;
    private String msg;


    BizCodeEnum(int code, String msg) {
        this.code=code;
        this.msg=msg;
    }
}
