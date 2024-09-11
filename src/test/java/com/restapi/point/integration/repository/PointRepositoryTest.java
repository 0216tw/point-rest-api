package com.restapi.point.integration.repository;


import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.domain.service.UserService;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.infrastructure.repository.UserRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//단순 연동 테스트
@SpringBootTest
@Transactional
public class PointRepositoryTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PointRepository pointRepository;

    @Test
    @DisplayName("포인트 충전")
    public void 포인트_충전() {

        //given
        long userId = 1L;
        RequestDTO requestDTO = new RequestDTO(10000);
        userRepository.save(new User(userId , 0));

        //when
        User user = pointService.charge(1L, requestDTO);

        //then
        Assertions.assertThat(user.getUserId()).isEqualTo(1L);
        Assertions.assertThat(user.getPoint()).isEqualTo(10000);
    }

    @Test
    @DisplayName("포인트 사용")
    public void 포인트_사용() {

        //given
        long userId = 1L;
        RequestDTO requestDTO = new RequestDTO(20000);
        userRepository.save(new User(userId , requestDTO.getPoint()));

        //when
        User user = pointService.use(1L, new RequestDTO(5000));

        //then
        Assertions.assertThat(user.getUserId()).isEqualTo(1L);
        Assertions.assertThat(user.getPoint()).isEqualTo(15000);
    }

    @Test
    @DisplayName("포인트 조회")
    public void 포인트_조회() {

        //given
        long userId = 1L;
        RequestDTO requestDTO = new RequestDTO(20000);
        userRepository.save(new User(userId , requestDTO.getPoint()));

        //when
        long point = pointService.getPointById(1L);

        //then
        Assertions.assertThat(point).isEqualTo(requestDTO.getPoint());
    }

    @Test
    @DisplayName("포인트 이력 조회")
    public void 포인트_이력_조회() {

        //given
        long userId = 1L ;
        userRepository.save(new User(userId , 10000));

        pointRepository.save(new PointHistory( userId , 10000L , "charge"));
        pointRepository.save(new PointHistory( userId , 5000L , "use"));
        pointRepository.save(new PointHistory( userId , 3000L , "use"));
        pointRepository.save(new PointHistory( userId , 5000L , "charge"));

        //when
        List<PointHistory> pointHistories = pointService.getPointHistoriesById(userId);

        //then
        Assertions.assertThat(pointHistories.size()).isEqualTo(4);
        Assertions.assertThat(pointHistories.get(0).getPoint()).isEqualTo(10000L);
        Assertions.assertThat(pointHistories.get(3).getPoint()).isEqualTo(5000L);
    }



}
