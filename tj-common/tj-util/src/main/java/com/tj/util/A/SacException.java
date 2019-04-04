package com.tj.util.A;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.LOCKED, reason = "账号已经被锁定")
public class SacException extends RuntimeException {
    public SacException() {
        super();
    }

    public SacException(String message) {
        super(message);
    }

    public SacException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
