package com.usrun.backend.security;

import com.usrun.backend.config.ErrorCode;
import com.usrun.backend.exception.CodeException;
import com.usrun.backend.exception.ResourceNotFoundException;
import com.usrun.backend.model.User;
import com.usrun.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new CodeException(ErrorCode.USER_EMAIL_NOT_FOUND)
                );

        if(!user.isEnabled())
            new CodeException(ErrorCode.USER_DOES_NOT_PERMISSION);

        user.setLastLogin(Instant.now());
        userRepository.save(user);

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new CodeException(ErrorCode.USER_NOT_FOUND)
        );

        if(!user.isEnabled())
            throw new CodeException(ErrorCode.USER_DOES_NOT_PERMISSION);

        return UserPrincipal.create(user);
    }
}
