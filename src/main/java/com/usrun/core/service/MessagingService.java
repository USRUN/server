package com.usrun.core.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.usrun.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author phuctt4
 */

@Slf4j
@Service
public class MessagingService {

  public MessagingService() {
    FirebaseApp.initializeApp();
  }

  public boolean sendMessage(User user, String title, String body) {
    String token = user.getDeviceToken();
    if (StringUtils.isNotBlank(token)) {
      Message message = Message.builder()
          .setNotification(Notification.builder()
              .setTitle(title)
              .setBody(body)
              .build())
          .putData("title", title)
          .putData("body", body)
          .setToken(token)
          .build();

      try {
        FirebaseMessaging.getInstance().send(message);
        return true;
      } catch (FirebaseMessagingException e) {
        log.error("Send Message failed, user: {}, title: {}, body: {}, {}", user.getId(), title,
            body, e.getMessage(), e);
        return false;
      }
    }
    return false;
  }
}
