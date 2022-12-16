package com.example.demonats.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CarCurrentStateDto {   // 차량의 최신 상태 정보
    private final int serialNumber;
    private final int gear;
    private final String state;
    private final String stateTime;

    @Builder
    CarCurrentStateDto(int serialNumber, int gear, String stateTime) {
        this.serialNumber = serialNumber;
        this.gear = gear;
        this.state = (gear == 1 ? "대기" : "운행");
        this.stateTime = stateTime;
    }

//    정보 예시
//    {
//        "serialNumber": 8282,
//        "gear": 1,
//        "state": "대기중",
//        "stateTime": "1시간"
//    }
}
