package com.usrun.backend.service;

import com.usrun.backend.config.ErrorCode;
import com.usrun.backend.exception.BadRequestException;
import com.usrun.backend.model.User;
import com.usrun.backend.model.type.Gender;
import com.usrun.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User updateUser(Long userId, String name,
                           String deviceToken, Integer gender,
                           Instant birthday, Double weight, Double height) {
        User user = userRepository.findById(userId).get();

        if(name != null) user.setName(name);
        if(deviceToken != null) user.setDeviceToken(deviceToken);

        if(gender != null)
            switch (gender) {
                case 0:
                    user.setGender(Gender.MALE);
                    break;
                case 1:
                    user.setGender(Gender.FEMALE);
                    break;
            }

        if(birthday != null) user.setBirthday(birthday);
        if(weight != null) user.setWeight(weight);
        if(height != null) user.setHeight(height);

        return userRepository.save(user);
    }
}
