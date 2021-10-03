package com.intalio.intaliosyed.exception;

import com.intalio.intaliosyed.dto.BaseResponse;
import com.intalio.intaliosyed.model.AppError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {RegisterFailedException.class})
    public BaseResponse<AppError> error(RegisterFailedException e) {
        return e.getError();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<AppError>> error(Exception e) {
        log.error(e.getMessage(), e);
        BaseResponse<AppError> r = new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),new AppError("Something Went Wrong"), false);
        return new ResponseEntity<>(r, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
