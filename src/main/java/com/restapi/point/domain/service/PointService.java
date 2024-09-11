package com.restapi.point.domain.service;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.application.exception.BusinessException;
import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.model.User;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.infrastructure.repository.UserRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PointRepository pointRepository;


    public User charge(long userId , RequestDTO requestDTO) {

        //값 검증
        if(requestDTO.getPoint() <= 0) throw new BusinessException(400 , Messages.MUST_UPPER_ONE_POINT_CHARGE);

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) throw new BusinessException(400 , Messages.NO_USER);
        user.get().setPoint(user.get().getPoint() + requestDTO.getPoint());
        return userRepository.save(user.get());
    }


    public User use(long userId ,RequestDTO requestDTO) {

        //값 검증
        if(requestDTO.getPoint() <= 0) throw new BusinessException(400 , Messages.MUST_UPPER_ONE_POINT_USE);

        User user = userRepository.findById(userId).get();
        System.out.println( "금액 체크 : " + (user.getPoint() - requestDTO.getPoint()));
        if(user.getPoint() - requestDTO.getPoint() < 0) throw new BusinessException(400 , Messages.LACK_POINT);
        user.setPoint(user.getPoint() - requestDTO.getPoint());
        return userRepository.save(user);
    }

    public long getPointById(long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()) throw new BusinessException(400 , Messages.NO_USER);

        return user.get().getPoint();
    }

    public List<PointHistory> getPointHistoriesById(long userId) {

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) throw new BusinessException(400 , Messages.NO_USER);

        return pointRepository.findByUserId(userId);
    }


    //동시성 제어 테스트 : synchronized 활용 (포인트 충전 & 사용)
    public synchronized User chargeBySynchronized(long userId , RequestDTO requestDTO) { return charge(userId , requestDTO);}
    public synchronized User useBySynchronized(long userId , RequestDTO requestDTO) {
        return use(userId , requestDTO);
    }

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    public Lock getLockForUser(long userId) {
        return lockMap.computeIfAbsent(userId , id -> new ReentrantLock());
    }
    public User chargeByReentrantLock(long userId , RequestDTO requestDTO) {
        Lock userLock = getLockForUser(userId);
        userLock.lock();
        try {
            return charge(userId, requestDTO);
        } finally {
            userLock.unlock();
        }
    }
    public User useByReentrantLock(long userId , RequestDTO requestDTO) {
        Lock userLock = getLockForUser(userId);
        userLock.lock();
        try {
            return use(userId , requestDTO);
        } finally {
            userLock.unlock();
        }
    }



    //동시성 제어 테스트 : 비관락 활용
    @Transactional
    public User chargeByPessimisticLock(long userId , RequestDTO requestDTO) {

        System.out.println("충전 진입");

        Optional<User> user = userRepository.findByIdUsingPessimisticLock(userId);
        if(user.isEmpty()) {
            System.out.println("사용자 없음");
            throw new BusinessException(400 , Messages.NO_USER);
        }

        //값 검증
        if(requestDTO.getPoint() <= 0) throw new BusinessException(400 , Messages.MUST_UPPER_ONE_POINT_CHARGE);

        user.get().setPoint(user.get().getPoint() + requestDTO.getPoint());
        System.out.println("충전 내역 저장");
        return userRepository.save(user.get());
    }

    @Transactional
    public User useByPessimisticLock(long userId , RequestDTO requestDTO) {

        System.out.println("사용 진입");

        User user = userRepository.findByIdUsingPessimisticLock(userId).get();
        if(user.getPoint() - requestDTO.getPoint() < 0) {
            System.out.println("돈 없음");
            throw new BusinessException(400 , Messages.LACK_POINT);
        }

        //값 검증
        if(requestDTO.getPoint() <= 0) throw new BusinessException(400 , Messages.MUST_UPPER_ONE_POINT_USE);

        user.setPoint(user.getPoint() - requestDTO.getPoint());
        System.out.println("사용 내역 저장");

        return userRepository.save(user);
    }


}
