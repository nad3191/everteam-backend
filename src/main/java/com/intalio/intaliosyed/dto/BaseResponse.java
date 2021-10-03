package com.intalio.intaliosyed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {

    private final Integer status;
    private final T data;
    private final boolean success;
}
