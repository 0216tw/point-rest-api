package com.restapi.point.application.enums;


//애플리케이션 전반에 사용되는 메세지 , 에러코드 관리
public enum Messages {
    CHARGE_SUCCESS("충전완료") ,
    USE_SUCCESS("사용완료") ,

    SEARCH_SUCCESS("조회완료") ,
    POINT_HISTORY_SEARCH_SUCCESS("포인트 이력 조회 완료") ,

    //오류 케이스
    MUST_UPPER_ONE_POINT_CHARGE("1원 이상 충전가능합니다."),

    MUST_UPPER_ONE_POINT_USE("1원 이상 사용가능합니다.") ,

    BAD_REQUEST("잘못된 요청입니다. 파라미터/URL을 확인하세요") ,

    LACK_POINT("잔액이 부족합니다.") ,

    NO_USER("없는 사용자입니다")


    ;

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
