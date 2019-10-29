package com.usrun.backend.service;

import com.usrun.backend.config.ErrorCode;
import com.usrun.backend.exception.BadRequestException;
import com.usrun.backend.model.User;
import com.usrun.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User updateUser(Long userId, String name, String deviceToken) {
        User user = userRepository.findById(userId).get();

        if(name != null) user.setName(name);

        if(deviceToken != null) user.setDeviceToken(deviceToken);

        return userRepository.save(user);
    }
}
