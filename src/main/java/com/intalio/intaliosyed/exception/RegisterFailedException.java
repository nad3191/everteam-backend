package com.intalio.intaliosyed.exception;

import com.intalio.intaliosyed.dto.BaseResponse;
import com.intalio.intaliosyed.model.AppError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RegisterFailedException extends Exception {

    private final BaseResponse<AppError> error;

    public RegisterFailedException(String message, HttpStatus status) {
        super(message);
        this.error = new BaseResponse<>(status.value(), new AppError(message), false);
    }
}
