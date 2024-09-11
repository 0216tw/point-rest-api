package com.restapi.point.presentation.dto;
import lombok.Data;

@Data
public class ResponseDTO<T> {
    private int code;
    private String message ;
    private T data;

    public ResponseDTO(int code , String message, T data) { //데이터 존재할 경우
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(int code , String message) { //데이터가 없는 경우
        this.code = code;
        this.message = message;
    }
}
