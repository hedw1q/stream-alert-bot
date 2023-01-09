package com.hedw1q.honey_alerts.telegram.ex;

public class ApiException extends RuntimeException{
    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }
}
