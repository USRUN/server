package com.usrun.core.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usrun.core.config.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeResponse {

  private ErrorCode code;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String errorMessage;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object data;

  public CodeResponse(int code) {
    this.code = ErrorCode.fromInt(code);
    this.errorMessage = this.code.name();
  }

  public CodeResponse(Object data) {
    this.code = ErrorCode.SUCCESS;
    this.errorMessage = ErrorCode.SUCCESS.name();
    this.data = data;
  }

  public CodeResponse(ErrorCode errorCode) {
    this.code = errorCode;
    this.errorMessage = errorCode.name();
  }
}
