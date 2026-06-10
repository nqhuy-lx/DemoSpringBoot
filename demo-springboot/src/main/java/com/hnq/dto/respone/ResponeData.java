package com.hnq.dto.respone;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatusCode;

public class ResponeData<T> {
    private final int status;

    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    // put patch delete
    public ResponeData(int status, String message) {
        this.status = status;
        this.message = message;
    }
    //post get
    public ResponeData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
