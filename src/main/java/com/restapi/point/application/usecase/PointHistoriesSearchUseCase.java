package com.restapi.point.application.usecase;

import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.infrastructure.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointHistoriesSearchUseCase {
    @Autowired
    PointService pointService ;
    public List<PointHistory> getPointHistoriesById(long userId) {
        return pointService.getPointHistoriesById(userId);
    }
}
