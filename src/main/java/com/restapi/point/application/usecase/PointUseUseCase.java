package com.restapi.point.application.usecase;

import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointUseUseCase {

    //1. 포인트 사용
    //2. 포인트 내역 추가
    @Autowired
    PointService pointService;

    @Autowired
    PointRepository pointRepository;

    public User use(long userId , RequestDTO requestDTO) {
        User user = pointService.useByPessimisticLock(userId , requestDTO);
        pointRepository.save(new PointHistory(userId , requestDTO.getPoint() , "사용"));
        return user;
    }

}
