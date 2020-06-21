package com.usrun.core.security;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.User;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.service.UserService;
import com.usrun.core.utility.CacheClient;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private CacheClient cacheClient;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userService.loadUser(email);

    if (!user.isEnabled()) {
      new CodeException(ErrorCode.USER_DOES_NOT_PERMISSION);
    }

    user.setLastLogin(new Date());
    userRepository.update(user);
    cacheClient.setUser(user);

    return UserPrincipal.create(user);
  }

  public UserDetails loadUserById(Long id) {
    User user = userService.loadUser(id);

    if (!user.isEnabled()) {
      throw new CodeException(ErrorCode.USER_DOES_NOT_PERMISSION);
    }

    return UserPrincipal.create(user);
  }
}
