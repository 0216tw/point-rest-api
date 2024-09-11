package com.restapi.point.integration.concurrency;


/*
ReentrantLock 를 활용해 단일 사용자 환경에서 다중 요청을 한 경우 테스트

- Synchronized 보다 세밀한 락 제어가 가능하다.
- 명시적인 락을 사용할 수 있으며 공정성 , 타임아웃 처리 등을 제공한다.
- 공정성 예 : new ReentrantLock(true) : 스레드가 락을 기다린 순서대로 처리 가능 (성능 지연 가능)
- 타임아웃 지원 : tryLock(timeout , unit) 으로 특정 시간동안 락 대기 및 실패하면 다른 작업을 하도록 한다.



1) 충전을 여러번 하는 경우
2) 사용을 여러번 하는 경우
3) 충전 , 사용을 번갈아 여러번 하는 경우
*/

import com.restapi.point.application.exception.BusinessException;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.infrastructure.repository.UserRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class SingleUserMultipleRequestReentrantLockTest {


    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(new User(126L, 0));
    }

    @Test
    @DisplayName("단일 사용자가 여러 충전을 시도하는 경우")
    public void 단일_사용자가_여러_충전_시도_재진입락_사용() throws InterruptedException {
        //given
        int numberOfRequests = 10 ;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);
        long userId = 126L ;

        //when

        for(int i = 0 ; i<numberOfRequests; i++) {
            executorService.submit(() -> {
                RequestDTO requestDto = new RequestDTO(10000);
                pointService.chargeByReentrantLock(userId , requestDto);
            });
        }
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            // 만약 작업이 10초 내에 완료되지 않으면 강제로 종료
            executorService.shutdownNow();
        }

        Assertions.assertThat(pointService.getPointById(userId)).isEqualTo(100000);
    }

    @Test
    @DisplayName("단일 사용자가 여러 사용을 시도하는 경우")
    public void 단일_사용자가_여러_사용_시도_재진입락_사용() throws InterruptedException {

        //given
        long userId = 126L ;
        userRepository.save(new User(126L , 70000));
        int numberOfRequests = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);
        List<Future> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);


        //when
        for(int i = 0 ; i<numberOfRequests; i++) {

            futures.add(executorService.submit(() -> {
                pointService.useByReentrantLock(userId , new RequestDTO(10000));
            }));
        }

        for(int i = 0 ; i<numberOfRequests; i++) {
            try {
                futures.get(i).get();
                successCount.incrementAndGet();
            } catch(Exception e) {
                failCount.incrementAndGet();
            }
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            // 10초 내에 완료되지 않으면 강제 종료
            executorService.shutdownNow();
        }

        //then
        Assertions.assertThat(successCount.get()).isEqualTo(7);
        Assertions.assertThat(failCount.get()).isEqualTo(3);


    }

//    @Test
//    @DisplayName("단일 사용자가 여러 충전/사용을 시도하는 경우 - 실패!! 어떻게 하면 처리할 수 있을까?")
//    public void 단일_사용자가_여러_충전_사용_시도_재진입락_사용() throws InterruptedException {
//
//
//        //given
//        long userId = 126L ;
//        int numberOfRequests = 10;
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);
//        List<Future> futures = new ArrayList<>();
//        AtomicInteger successCount = new AtomicInteger(0);
//        AtomicInteger failCount = new AtomicInteger(0);
//
//
//        //when
//        for(int i = 0 ; i<numberOfRequests; i++) {
//
//            final int finalI = i;
//
//            futures.add(executorService.submit(() -> {
//                try {
//                    if(finalI % 2 == 0) {
//                        pointService.useByReentrantLock(userId , new RequestDTO(5000));
//                    } else {
//                        pointService.chargeByReentrantLock(userId , new RequestDTO(10000));
//                    }
//                } catch (BusinessException businessException) {
//                    System.out.println("잔액 부족!");
//                }
//            }));
//
//        }
//
//        for(int i = 0 ; i<numberOfRequests; i++) {
//            try {
//                futures.get(i).get();
//                successCount.incrementAndGet();
//            } catch(Exception e) {
//                failCount.incrementAndGet();
//            }
//        }
//
//        executorService.shutdown();
//        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
//            // 10초 내에 완료되지 않으면 강제 종료
//            executorService.shutdownNow();
//        }
//
//        //then
//        Assertions.assertThat(pointService.getPointById(126L)).isEqualTo(30000);
//
//    }


}
