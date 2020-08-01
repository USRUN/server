package com.usrun.core.service;

import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Role;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.payload.dto.UserDTO;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.security.TokenProvider;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.UniqueGenerator;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private TemplateEngine templateEngine;

  @Autowired
  private AmazonClient amazonClient;

  @Autowired
  private CacheClient cacheClient;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UniqueGenerator uniqueGenerator;

  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private TokenProvider tokenProvider;

  public User createUser(String name, String email, String password) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setType(AuthType.local);
    user.setPassword(passwordEncoder.encode(password));
    user.setRoles(Collections.singleton(new Role(RoleType.ROLE_USER)));
    user.setCreateTime(new Date());
    user.setUpdateTime(new Date());
    user.setAvatar(appProperties.getDefaultAvatar());
    uniqueGenerator.generateID(user);
    user = userRepository.insert(user);
    cacheClient.setUser(user);

    if (email.endsWith("@student.hcmus.edu.vn")) {
      try {
        sendEmailOTP(user.getId(), email);
      } catch (MessagingException e) {
        e.printStackTrace();
      }
    }
    return user;
  }

  public User loadUser(long userId) {
    User user = cacheClient.getUser(userId);
    if (user == null) {
      user = userRepository.findById(userId);
      if (user == null) {
        throw new CodeException(ErrorCode.USER_NOT_FOUND);
      }
      user.setTeams(teamRepository.getTeamsByUser(userId));
      cacheClient.setUser(user);
    }
    return user;
  }

  public User loadUser(String email) {
    User user = cacheClient.getUser(email);
    if (user == null) {
      user = userRepository.findUserByEmail(email);
      if (user == null) {
        throw new CodeException(ErrorCode.USER_EMAIL_NOT_FOUND);
      }
      user.setTeams(teamRepository.getTeamsByUser(user.getId()));
      cacheClient.setUser(user);
    }
    return user;
  }

  public User verifyUser(String email, String password) {
    User user = cacheClient.getUser(email);
    if (user == null) {
      user = userRepository.findUserByEmail(email);
      if (user == null) {
        throw new CodeException(ErrorCode.USER_LOGIN_FAIL);
      }
      user.setTeams(teamRepository.getTeamsByUser(user.getId()));
      cacheClient.setUser(user);
    }

    if (passwordEncoder.matches(password, user.getPassword()) && user.isEnabled()) {
      return user;
    } else {
      return null;
    }
  }

  public User updateUser(Long userId, String name,
      String deviceToken, Integer gender,
      Date birthday, Double weight, Double height,
      String base64Image, Integer province) {
    User user = userRepository.findById(userId);

    if (name != null) {
      user.setName(name);
    }
    if (deviceToken != null) {
      user.setDeviceToken(deviceToken);
    }

    if (gender != null) {
      switch (gender) {
        case 0:
          user.setGender(Gender.MALE);
          break;
        case 1:
          user.setGender(Gender.FEMALE);
          break;
      }
    }

    if (birthday != null) {
      user.setBirthday(birthday);
    }
    if (weight != null) {
      user.setWeight(weight);
    }
    if (height != null) {
      user.setHeight(height);
    }

    if (base64Image != null) {
      if (base64Image.length() > appProperties.getMaxImageSize()) {
        throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
      }
      String fileUrl = amazonClient
          .uploadFile(base64Image, "avatar-" + userId + System.currentTimeMillis());
      if (fileUrl != null) {
        amazonClient.deleteFile(user.getAvatar());
        user.setAvatar(fileUrl);
      }
    }

    if (province != null && province > 0 && province <= 63) {
      user.setProvince(province);
    }

    userRepository.update(user);
    cacheClient.setUser(user);

    return user;
  }

  public User banUser(long userId, boolean isBanned) {
    User user = loadUser(userId);
    user.setEnabled(!isBanned);
    userRepository.update(user);
    cacheClient.setUser(user);
    return user;
  }

  public User changePassword(long userId, String oldPassword, String newPassword) {
    User user = loadUser(userId);
    if (passwordEncoder.matches(oldPassword, user.getPassword())) {
      if (StringUtils.isNotBlank(newPassword)) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.update(user);
        cacheClient.setUser(user);
        return user;
      } else {
        throw new CodeException(ErrorCode.INVALID_PARAM);
      }
    } else {
      throw new CodeException(ErrorCode.USER_DOES_NOT_PERMISSION);
    }
  }

  public Boolean verifyOTP(Long userId, String otp) {
    boolean verified = cacheClient.verifyOTPFromCache(userId, otp);

    if (verified) {
      User user = userRepository.findById(userId);
      user.setHcmus(true);
      userRepository.update(user);
      cacheClient.setUser(user);
    }
    return verified;
  }

  @Async("threadPoolEmailOtp")
  public void sendEmailOTP(Long userId, String email) throws MessagingException {
    String otp = cacheClient.generateAndSaveOTP(userId);

    Context context = new Context();
    context.setVariable("otp", otp);
    String content = templateEngine.process("mail-otp-template", context);

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

    helper.setTo(email);
    helper.setSubject("[USRUN] Xác thực email sinh viên HCMUS");
    helper.setText(content, true);

    javaMailSender.send(message);
  }

  public void sendEmailResetPassword(String email, String newPassword) throws MessagingException {
    Context context = new Context();
    context.setVariable("newPassword", newPassword);
    String content = templateEngine.process("mail-reset-password-template", context);

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

    helper.setFrom("USRUN");
    helper.setTo(email);
    helper.setSubject("[USRUN] Làm mới mật khẩu tài khoản USRUN");
    helper.setText(content, true);

    javaMailSender.send(message);
  }

  @Async("threadPoolEmailOtp")
  public void resetPassword(User user) {
    String newPassword = uniqueGenerator.randomString(10);
    try {
      sendEmailResetPassword(user.getEmail(), newPassword);
      user.setPassword(passwordEncoder.encode(newPassword));
      userRepository.update(user);
      cacheClient.setUser(user);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  public List<UserDTO> getUserDTOs(Set<Long> users) {
    return userRepository.findAll(users);
  }

  public UserDTO getUserDTO(long userId) {
    List<UserDTO> userDTOS = userRepository.findAll(Collections.singleton(userId));
    return userDTOS.isEmpty() ? null : userDTOS.get(0);
  }
}
