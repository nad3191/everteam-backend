package com.intalio.intaliosyed.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class AppError implements Serializable {
    @NonNull
    private String message;
}
