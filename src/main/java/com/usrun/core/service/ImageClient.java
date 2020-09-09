package com.usrun.core.service;

import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.config.ImgurConfig;
import com.usrun.core.exception.CodeException;
import com.usrun.core.payload.UploadImageResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ImageClient {

  private final AppProperties appProperties;

  private final Map<String, String> typeMap;

  private final RestTemplate restTemplate;

  private final ImgurConfig imgurConfig;

  public ImageClient(AppProperties appProperties,
      RestTemplate restTemplate, ImgurConfig imgurConfig) {
    this.appProperties = appProperties;
    this.typeMap = new HashMap<>();
    this.typeMap.put("data:image/jpeg;base64", "jpeg");
    this.typeMap.put("data:image/png;base64", "png");
    this.typeMap.put("data:image/jpg;base64", "jpg");
    this.restTemplate = restTemplate;
    this.imgurConfig = imgurConfig;
  }

  public String uploadFileWithLimitation(String content) {
    if (content.startsWith("http")) {
      return content;
    } else {
      if (content.length() <= appProperties.getMaxImageSize()) {
        String base64Content = getBase64Content(content);
        return uploadFile(base64Content);
      } else {
        throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
      }
    }
  }

  private String getBase64Content(String base64) {
    String[] parts = base64.split(",");
    if (typeMap.get(parts[0]) == null) {
      throw new CodeException(ErrorCode.IMAGE_INVALID);
    }
    return parts[1];
  }

  public String uploadFile(BufferedImage bufferedImage, String extensions) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(bufferedImage, extensions, baos);
      if (baos.size() > appProperties.getMaxImageSize()) {
        throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
      }
      final byte[] bytes = baos.toByteArray();
      return uploadFile(Base64.encodeBase64String(bytes));
    } catch (IOException e) {
      log.error("", e);
    }
    return null;
  }

  public String uploadFile(String content) {
    String url = "https://api.imgur.com/3/image";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Client-ID " + imgurConfig.getClientId());
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("image", content);
    map.add("type", "base64");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
        map, headers);
    try {
      ResponseEntity<UploadImageResponse> response = restTemplate
          .postForEntity(url, request, UploadImageResponse.class);
      UploadImageResponse resData = response.getBody();
      if (resData.isSuccess()) {
        return resData.getData().getLink();
      } else {
        log.error("Upload image failed");
      }
    } catch (Exception e) {
      log.error("Upload image failed ", e);
    }
    return null;
  }

}
