package com.restapi.point.application.usecase;

import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointSearchUseCase {

    @Autowired
    PointService pointService;

    @Autowired
    PointRepository pointRepository;

    public long getPointById(long userId) {
        return pointService.getPointById(userId);
    }
}
