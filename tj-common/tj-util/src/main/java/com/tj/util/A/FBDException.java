package com.tj.util.A;

public class FBDException extends RuntimeException {
    public FBDException() {
        super();
    }

    public FBDException(String message) {
        super(message);
    }

    public FBDException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
