package com.restapi.point.application.exception;

import com.restapi.point.application.enums.Messages;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private int errorCode;

    public BusinessException(Messages message) {
        super(message.toString());
    }

    public BusinessException(int errorCode , Messages message) {
        super(message.toString());
        this.errorCode = errorCode;
    }
}
