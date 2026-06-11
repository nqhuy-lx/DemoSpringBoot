package com.hnq.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class SampleDTO implements Serializable {
    private Integer id;
    private String message;
}
