package com.usrun.core.exception;

import com.usrun.core.config.ErrorCode;

/**
 * @author phuctt4
 */
public class TrackException extends CodeException {
    public TrackException(String message, int errorCode) {
        super(message, errorCode);
    }
}
