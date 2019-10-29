package com.usrun.backend.exception;

import com.usrun.backend.config.ErrorCode;

public class CodeException extends RuntimeException {
    private int errorCode;

    public CodeException(int errorCode) {
        super(Integer.toString(errorCode));
        this.errorCode = errorCode;
    }
}
