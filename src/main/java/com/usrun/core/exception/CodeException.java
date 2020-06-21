package com.usrun.core.exception;

public class CodeException extends RuntimeException {

  private int errorCode;

  public CodeException(int errorCode) {
    super(Integer.toString(errorCode));
    this.errorCode = errorCode;
  }

  public CodeException(String message, int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return errorCode;
  }
}
