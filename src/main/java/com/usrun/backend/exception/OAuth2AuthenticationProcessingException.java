package com.usrun.backend.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {
    private int code;
    public OAuth2AuthenticationProcessingException(String msg, Throwable t, int code) {
        super(msg, t);
        this.code = code;
    }

    public OAuth2AuthenticationProcessingException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
