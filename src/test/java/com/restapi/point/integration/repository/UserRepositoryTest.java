package com.restapi.point.integration.repository;


import com.restapi.point.domain.model.User;
import com.restapi.point.domain.service.UserService;
import com.restapi.point.infrastructure.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

//단순 연동 테스트
@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("사용자존재유무-사용자가 존재하는 경우")
    public void 사용자가_존재하는_경우() {

        //given
        User user = new User(1L , 10000L);
        userRepository.save(user);
        //when
        boolean isUserExists = userService.isUserExists(1L);
        //then
        Assertions.assertThat(isUserExists).isTrue();
    }

    @Test
    @DisplayName("사용자존재유무-사용자가 존재하지 않는 경우")
    public void 사용자가_존재하지않는_경우() {
        //given

        //when
        boolean isUserExists = userService.isUserExists(112684863457L);
        //then
        Assertions.assertThat(isUserExists).isFalse();
    }


}
