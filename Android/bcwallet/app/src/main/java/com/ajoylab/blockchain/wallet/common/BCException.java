package com.ajoylab.blockchain.wallet.common;

public class BCException extends Exception {

    public final int code;

    public BCException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BCException(int code, String message) {
        super(message, null);
        this.code = code;
    }
}
