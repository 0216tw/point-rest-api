package com.restapi.point.domain.service;

import com.restapi.point.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    public boolean isUserExists(long userId) {
        return userRepository.existsById(userId);
    }
}
