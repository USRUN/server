package com.usrun.core.exception;

import com.usrun.core.config.ErrorCode;

public class CodeException extends RuntimeException {

  private ErrorCode errorCode;

  public CodeException(int errorCode) {
    super(Integer.toString(errorCode));
    this.errorCode = ErrorCode.fromInt(errorCode);
  }

  public CodeException(String message, int errorCode) {
    super(message);
    this.errorCode = ErrorCode.fromInt(errorCode);
  }

  public CodeException(ErrorCode errorCode) {
    super(errorCode.name());
    this.errorCode = errorCode;
  }

  public CodeException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
