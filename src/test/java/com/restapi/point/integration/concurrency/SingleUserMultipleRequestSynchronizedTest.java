package com.restapi.point.integration.concurrency;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
synchronized 를 활용해 단일 사용자 환경에서 다중 요청을 한 경우 테스트

1) 충전을 여러번 하는 경우
2) 사용을 여러번 하는 경우
3) 충전 , 사용을 번갈아 여러번 하는 경우
*/

@SpringBootTest
public class SingleUserMultipleRequestSynchronizedTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        userRepository.save(new User(125L , 100000));
    }

    @Test
    @DisplayName("단일 사용자 충전 여러번 요청 - synchronized")
    public void 단일_사용자가_여러_충전을_요청할_경우() throws InterruptedException {

        //given
        int numberOfRequests = 10; //동시 10회 요청
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests) ;

        //when
        for(int i = 0; i<numberOfRequests; i++) {
            RequestDTO requestDTO = new RequestDTO(10000);
            executorService.submit(() -> pointService.chargeBySynchronized(125L, requestDTO));
        }
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            // 만약 작업이 10초 내에 완료되지 않으면 강제로 종료
            executorService.shutdownNow();
        }

        //then
        Assertions.assertThat(pointService.getPointById(125L)).isEqualTo(200000);


    }

    @Test
    @DisplayName("단일 사용자 사용 여러번 요청 - synchronized")
    public void 단일_사용자가_여러_사용을_요청할_경우() throws InterruptedException {

        //given
        int numberOfRequests = 10; //동시 10회 요청
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests) ;

        //when
        for(int i = 0; i<numberOfRequests; i++) {
            RequestDTO requestDTO = new RequestDTO(5000);
            executorService.submit(() -> pointService.useBySynchronized(125L, requestDTO));
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            // 만약 작업이 10초 내에 완료되지 않으면 강제로 종료
            executorService.shutdownNow();
        }

        //then
        Assertions.assertThat(pointService.getPointById(125L)).isEqualTo(50000);


    }


    @Test
    @DisplayName("단일 사용자 충전/사용 여러번 요청 - synchronized")
    public void 단일_사용자가_여러_충전_사용을_번갈아_요청한_경우() throws InterruptedException {

        //given
        int numberOfRequests = 10; //동시 10회 요청
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests) ;

        // 비동기 작업을 저장할 리스트
        List<Future<?>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for(int i = 0; i<numberOfRequests; i++) {

            int finalI = i;

            futures.add(executorService.submit(() -> {

                    if (finalI % 2 == 0) {
                        RequestDTO requestDTO = new RequestDTO(5000);
                        pointService.useBySynchronized(125L, requestDTO);
                    } else {
                        RequestDTO requestDTO = new RequestDTO(10000);
                        pointService.chargeBySynchronized(125L, requestDTO);
                    }

            }));
        }
        executorService.shutdown();

        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            // 만약 작업이 10초 내에 완료되지 않으면 강제로 종료
            executorService.shutdownNow();
        }


        // 모든 비동기 작업의 결과를 기다림 (안그러면 서브 스레드가 예외를 메인 스레드에 보내주지 않음)
        for (Future<?> future : futures) {
            try {
                future.get();  // 여기서 작업 중 발생한 예외가 메인 스레드로 전달됨
                successCount.incrementAndGet();
            } catch (Exception e) {
                // 필요하다면 추가적인 예외 처리 가능
                System.out.println("예외 발생: " + e.getMessage());
                failCount.addAndGet(1);
            }
        }


        //then
        Assertions.assertThat(pointService.getPointById(125L)).isEqualTo(125000);
        Assertions.assertThat(successCount.get()).isEqualTo(10);

    }
}
