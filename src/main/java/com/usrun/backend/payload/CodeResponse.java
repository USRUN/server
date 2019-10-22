package com.usrun.backend.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeResponse {
    private int code;

    public CodeResponse(int code) {
        this.code = code;
    }
}
