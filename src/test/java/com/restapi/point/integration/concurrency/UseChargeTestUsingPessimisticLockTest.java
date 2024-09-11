package com.restapi.point.integration.concurrency;

import com.restapi.point.application.exception.BusinessException;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.infrastructure.repository.UserRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class UseChargeTestUsingPessimisticLockTest {


    @Autowired
    PointService pointService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(new User(127L , 100000));
    }

    @Test
    @DisplayName("비관락을 활용해 충전/사용 테스트")
    public void 비관락_활용_충전_사용_테스트() throws InterruptedException {

        //given
        long userId = 127L ;
        int numberOfRequests = 50;
        int expected = 100000;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        //when
        for(int i = 0 ; i <numberOfRequests; i++) {

            try {
                int finalI = i;
                executorService.submit(() -> {
                    if(finalI % 2 == 0) {
                        pointService.useByPessimisticLock(userId , new RequestDTO(1000));
                    } else {
                        pointService.chargeByPessimisticLock(userId , new RequestDTO(1000));
                    }
                });
            } catch( BusinessException e) {
                System.out.println("예외 발생: " + e.getMessage());
            }
        }

        executorService.shutdown(); // 작업이 모두 제출된 후 shutdown 호출
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            // 만약 작업이 10초 내에 완료되지 않으면 강제로 종료
            executorService.shutdownNow();
        }

        //then
        Assertions.assertThat(pointService.getPointById(userId)).isEqualTo(expected);

    }
}
