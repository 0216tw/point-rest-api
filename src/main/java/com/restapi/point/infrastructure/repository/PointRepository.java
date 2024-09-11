package com.restapi.point.infrastructure.repository;

import com.restapi.point.domain.model.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<PointHistory , Long> {

    List<PointHistory> findByUserId(long userId);

}
