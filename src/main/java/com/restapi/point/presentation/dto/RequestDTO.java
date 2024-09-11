package com.restapi.point.presentation.dto;


import lombok.Data;

@Data
public class RequestDTO {
    private long point;

    public RequestDTO() { //Jackson은 객체를 직렬화할 때 기본적으로 기본 생성자를 사용
    }

    public RequestDTO(long point) {
        this.point = point;
    }
}
