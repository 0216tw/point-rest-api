package com.restapi.point.application.usecase;

import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointChargeUseCase {

    //1. 포인트 충전
    //2. 포인트 내역 추가
    @Autowired
    PointService pointService;

    @Autowired
    PointRepository pointRepository;


    public User charge(long userId , RequestDTO requestDTO) {
        User user = pointService.chargeByPessimisticLock(userId , requestDTO);
        pointRepository.save(new PointHistory(userId , requestDTO.getPoint() , "충전"));
        return user;
    }

}
