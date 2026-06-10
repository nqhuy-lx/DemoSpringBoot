package com.hnq.dto.respone;

public class ResponeError extends ResponeData{
    public ResponeError(int status, String message)
    {
        super(status, message);
    }
}
