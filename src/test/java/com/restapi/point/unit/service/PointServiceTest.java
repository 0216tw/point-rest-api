package com.restapi.point.unit.service;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.application.exception.BusinessException;
import com.restapi.point.domain.model.PointHistory;
import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.PointService;
import com.restapi.point.infrastructure.repository.PointRepository;
import com.restapi.point.infrastructure.repository.UserRepository;
import com.restapi.point.presentation.dto.RequestDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PointRepository pointRepository;

    @InjectMocks
    PointService pointService;


    //포인트 충전
    @Test
    @DisplayName("성공-포인트를 충전한다.")
    public void 성공_포인트_충전() {

        //given
        long userId = 1L ;
        RequestDTO requestDTO = new RequestDTO(10000);
        User user = new User(userId , 50000);

        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)) ;
        when(userRepository.save(any(User.class))).thenAnswer(

                invocation -> {
                    user.setPoint(user.getPoint() + requestDTO.getPoint());
                    return user;
                });

        User chargedUser = pointService.charge(userId , requestDTO);

        //then
        Assertions.assertThat(chargedUser.getUserId()).isEqualTo(user.getUserId());
        Assertions.assertThat(chargedUser.getPoint()).isEqualTo(user.getPoint());
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    @DisplayName("실패-충전금액이 0원 이하인 경우")
    public void 실패_충전금액이_0원_이하() {

        //given
        long userId = 1L ;
        RequestDTO requestDTO = new RequestDTO(0);

        //when & then
        // 예외가 발생하는지 검증
        assertThatThrownBy(() -> pointService.charge(userId , requestDTO))
                .isInstanceOf(BusinessException.class)  // 예외 타입 검증
                .hasMessage(Messages.MUST_UPPER_ONE_POINT_CHARGE.toString());  // 예외 메시지 검증

        verify(userRepository, never()).save(any(User.class));
    }

    //포인트 사용
    @Test
    @DisplayName("성공-포인트를 사용한다.")
    public void 성공_포인트_사용() {

        //given
        long userId = 1L ;
        RequestDTO requestDTO = new RequestDTO(10000);
        User user = new User(userId , 50000);

        //when

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)) ;
        when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> {
                    user.setPoint(user.getPoint() - requestDTO.getPoint());
                    return user;
                });

        User chargedUser = pointService.use(userId , requestDTO);

        //then
        Assertions.assertThat(chargedUser.getUserId()).isEqualTo(user.getUserId());
        Assertions.assertThat(chargedUser.getPoint()).isEqualTo(user.getPoint());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("실패-사용할 포인트가 0원 이하인 경우")
    public void 실패_사용할_포인트가_0원_이하인_경우() {

        //given
        long userId = 1L ;
        RequestDTO requestDTO = new RequestDTO(0);

        //when & then
        // 예외가 발생하는지 검증
        assertThatThrownBy(() -> pointService.use(userId , requestDTO))
                .isInstanceOf(BusinessException.class)  // 예외 타입 검증
                .hasMessage(Messages.MUST_UPPER_ONE_POINT_USE.toString());  // 예외 메시지 검증

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("실패-잔액이 부족한 경우")
    public void 실패_잔액이_부족한_경우() {

        //given
        long userId = 1L ;
        RequestDTO requestDTO = new RequestDTO(50000);
        User user = new User(userId , 10000);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //when & then
        // 예외가 발생하는지 검증
        assertThatThrownBy(() -> pointService.use(userId , requestDTO))
                .isInstanceOf(BusinessException.class)  // 예외 타입 검증
                .hasMessage(Messages.LACK_POINT.toString());  // 예외 메시지 검증

        verify(userRepository, never()).save(any(User.class));
    }

    // 포인트 조회

    @Test
    @DisplayName("성공-포인트를 조회한다")
    public void 성공_사용자_포인트_조회() {
        //given
        long userId = 1L ;
        long expectedPoint = 5999;

        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(userId , 5999)));
        long point = pointService.getPointById(userId);

        //then
        Assertions.assertThat(point).isEqualTo(expectedPoint);
    }

    @Test
    @DisplayName("실패-없는 사용자일 경우")
    public void 실패_사용자가_없어요() {

        //given
        long userId = 1L ;

        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // 예외가 발생하는지 검증
        assertThatThrownBy(() -> pointService.getPointById(userId))
                .isInstanceOf(BusinessException.class)  // 예외 타입 검증
                .hasMessage(Messages.NO_USER.toString());  // 예외 메시지 검증
    }


    // 포인트 이용 내역 조회
    @Test
    @DisplayName("성공-포인트 이용 내역 조회 성공")
    public void 성공_포인트_이용내역_조회에_성공한경우() {

        //given
        long userId = 1L ;
        List<PointHistory> expectedPointHistories = List.of(
                new PointHistory(1L , userId , 10000L , "charge") ,  // return 10000
                new PointHistory(2L , userId , 5000L , "use") ,      // return 5000
                new PointHistory(3L , userId , 3000L , "use" ),      // return 2000
                new PointHistory(4L , userId , 5000L , "charge")     // return 7000
        );
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(userId , 7000)));
        when(pointRepository.findByUserId(anyLong())).thenReturn(expectedPointHistories);

        List<PointHistory> pointHistories = pointService.getPointHistoriesById(userId);

        //then
        Assertions.assertThat(expectedPointHistories.size()).isEqualTo(pointHistories.size());
        Assertions.assertThat(expectedPointHistories.get(1).getPoint()).isEqualTo(pointHistories.get(1).getPoint());
        Assertions.assertThat(expectedPointHistories.get(2).getType()).isEqualTo(pointHistories.get(2).getType());
    }

    @Test
    @DisplayName("실패 - 사용자가 없는 경우")
    public void 실패_사용자_없는딩() {
        //given
        long userId = 1L ;

        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // 예외가 발생하는지 검증
        assertThatThrownBy(() -> pointService.getPointHistoriesById(userId))
                .isInstanceOf(BusinessException.class)  // 예외 타입 검증
                .hasMessage(Messages.NO_USER.toString());  // 예외 메시지 검증
    }


}
