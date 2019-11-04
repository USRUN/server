package com.usrun.backend.service;

import com.usrun.backend.config.ErrorCode;
import com.usrun.backend.exception.BadRequestException;
import com.usrun.backend.model.User;
import com.usrun.backend.model.type.Gender;
import com.usrun.backend.repository.UserRepository;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private AmazonClient amazonClient;

    public User updateUser(Long userId, String name,
                           String deviceToken, Integer gender,
                           Instant birthday, Double weight, Double height,
                           String base64Image) {
        User user = userRepository.findById(userId).get();

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

        return userRepository.save(user);
    }

    public String generateAndSaveOTP(Long userId) {
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        RBucket<String> rOtp = redissonClient.getBucket("users:otp:" + userId);
        rOtp.set(otp, 5, TimeUnit.MINUTES);
        return otp;
    }

    public Boolean verifyOTP(Long userId, String otp) {
        RBucket<String> rOtp = redissonClient.getBucket("users:otp:" + userId);
        return otp.equals(rOtp.getAndDelete());
    }

    public void sendEmailOTP(Long userId, String email) throws MessagingException {
        String otp = generateAndSaveOTP(userId);

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
