package com.zuihuibao.mq.util.enums;


/**
 * Created by millions on 2016/8/28.
 * 数据库常量
 */
public enum DbConst {
    SESSION_HAS_LOGIN(1),
    SESSION_APP_LOGIN(2),
    INS_ONLINE(1);

    private final byte value;

    DbConst(int value) {
        this.value = (byte) value;
    }

    public byte value(){
        return value;
    }
}
