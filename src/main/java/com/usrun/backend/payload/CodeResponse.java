package com.usrun.backend.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeResponse {
    private int code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public CodeResponse(int code) {
        this.code = code;
    }

    public CodeResponse(Object data) {
        this.code = 0;
        this.data = data;
    }
}
