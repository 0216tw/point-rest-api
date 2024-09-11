package com.restapi.point.application.exception;

import com.restapi.point.presentation.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDTO<Void>> handleBusinessException(BusinessException ex) {
        ResponseDTO<Void> response = new ResponseDTO<Void>(ex.getErrorCode() , ex.getMessage());
        System.out.println(ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode()).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResponseDTO<Void> response = new ResponseDTO<>(400 , ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        ResponseDTO<Void> response = new ResponseDTO<>(405 , "정당한 URL 요청이 아닙니다.");
        return ResponseEntity.status(405).body(response);
    }
}
