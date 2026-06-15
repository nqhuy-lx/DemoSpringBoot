package com.hnq.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> implements Serializable {
    int pageNo;
    int pageSize;
    int totalPage;
    T items;
}
