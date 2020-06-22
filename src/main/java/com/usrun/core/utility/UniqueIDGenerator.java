package com.usrun.core.utility;

import com.usrun.core.model.User;
import com.usrun.core.repository.UserRepository;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueIDGenerator {

  @Autowired
  private UserRepository userRepository;

  public void generateID(User toGrantID) {
    StringBuffer resultIDBuffer = new StringBuffer();
    int randomLength;
    int codeLength;

    if (toGrantID.getEmail().contains("@student.hcmus.edu.vn")) {
      resultIDBuffer.append("SKHTN");
      randomLength = 5;
      codeLength = 5;
    } else {
      resultIDBuffer.append("N");
      randomLength = 9;
      codeLength = 1;
    }

    if (userRepository.findUserByCode("N84afb591-") != null) {
      System.out.println("code existed");
    }

    resultIDBuffer.append(UUID.randomUUID().toString().replace("-", ""), 0, randomLength);

    while (userRepository.findUserByEmail(resultIDBuffer.toString()) != null) {
      resultIDBuffer.delete(codeLength, codeLength + randomLength);
      resultIDBuffer.append(UUID.randomUUID().toString().replace("-", ""), 0, randomLength);
    }

    toGrantID.setCode(resultIDBuffer.toString());
  }

  public Long generateTrackId(Long userId) {
    return Long.parseLong(Long.toString(new Date().getTime() / 1000) + userId);
  }
}
