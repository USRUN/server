package com.usrun.core.service;

import com.usrun.core.model.Role;
import com.usrun.core.model.type.RoleType;
import com.usrun.core.model.User;
import com.usrun.core.model.type.AuthType;
import com.usrun.core.model.type.Gender;
import com.usrun.core.repository.UserRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.UniqueIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.Date;

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
    private UniqueIDGenerator uniqueIDGenerator;

    public User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setType(AuthType.local);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(new Role(RoleType.ROLE_USER)));
        uniqueIDGenerator.generateID(user);
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

    public User loadUser(String email) throws Exception {
        User user = cacheClient.getUser(email);
        if (user == null) {
            try {
                user = userRepository.findUserByEmail(email);
                if (user == null)
                    throw new Exception("User Not Found");
                cacheClient.setUser(user);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return user;
//        return userRepository.findByEmail(email).orElseThrow(
//                () -> new Exception("User Not Found")
//        );
    }

    public User updateUser(Long userId, String name,
                           String deviceToken, Integer gender,
                           Date birthday, Double weight, Double height,
                           String base64Image) {
        User user = userRepository.findById(userId);

        if (name != null) user.setName(name);
        if (deviceToken != null) user.setDeviceToken(deviceToken);

        if (gender != null)
            switch (gender) {
                case 0:
                    user.setGender(Gender.MALE);
                    break;
                case 1:
                    user.setGender(Gender.FEMALE);
                    break;
            }

        if (birthday != null) user.setBirthday(birthday);
        if (weight != null) user.setWeight(weight);
        if (height != null) user.setHeight(height);
        if (base64Image != null) {
            String fileUrl = amazonClient.uploadFile(base64Image, "avatar-" + userId);
            if (fileUrl != null) user.setImg(fileUrl);
        }

        userRepository.update(user);
        cacheClient.setUser(user);

        return user;
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
        String content = templateEngine.process("mailTemplate", context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        helper.setTo(email);
        helper.setSubject("[USRUN] Xác thực email sinh viên HCMUS");
        helper.setText(content, true);

        javaMailSender.send(message);
    }
}
