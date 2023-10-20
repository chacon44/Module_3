package com.module3.exceptions;

import lombok.Getter;

@Getter
public class CustomizedExceptions extends RuntimeException {
    private final String description;
    private final ErrorCode code;

    public CustomizedExceptions(String description, ErrorCode code) {
        super(description);
        this.description = description;
        this.code = code;
    }

}
