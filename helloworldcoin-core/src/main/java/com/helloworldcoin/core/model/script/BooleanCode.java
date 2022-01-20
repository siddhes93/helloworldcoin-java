package com.helloworldcoin.core.model.script;

/**
 * 布尔枚举
 *
 * @author x.king xdotking@gmail.com
 */
public enum BooleanCode {
    FALSE(new byte[]{(byte)0x00}),
    TRUE(new byte[]{(byte)0x01});

    private byte[] code;
    BooleanCode(byte[] code) {
        this.code = code;
    }

    public byte[] getCode() {
        return code;
    }
}
