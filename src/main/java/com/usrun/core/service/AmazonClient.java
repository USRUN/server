package com.usrun.core.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usrun.core.config.AmazonS3Config;
import com.usrun.core.config.AppProperties;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AmazonClient {

  private AmazonS3 amazonS3;

  @Autowired
  private AmazonS3Config amazonS3Config;

  @Autowired
  private AppProperties appProperties;

  @PostConstruct
  private void init() {
    AWSCredentials credentials = new BasicAWSCredentials(amazonS3Config.getAccessKey(),
        amazonS3Config.getSecretKey());
    this.amazonS3 = new AmazonS3Client(credentials);
  }

  private File convertBase64ToImage(String base64Image) throws IOException {
    String[] parts = base64Image.split(",");
    String extensions;

    switch (parts[0]) {
      case "data:image/jpeg;base64":
        extensions = "jpeg";
        break;
      case "data:image/png;base64":
        extensions = "png";
        break;
      case "data:image/jpg;base64":
        extensions = "jpg";
        break;
      default:
        throw new CodeException(ErrorCode.IMAGE_INVALID);
    }

    byte[] data = Base64.decodeBase64(parts[1]);

    String name = UUID.randomUUID().toString() + "." + extensions;

    BufferedImage image = null;

    ByteArrayInputStream bis = new ByteArrayInputStream(data);
    image = ImageIO.read(bis);
    bis.close();

    File outputFile = new File(name);
    ImageIO.write(image, extensions, outputFile);
    return outputFile;
  }

  public String uploadFileWithLimitation(String content, String fileName) {
    if (content.startsWith("http")) {
      return content;
    } else {
      if (content.length() <= appProperties.getMaxImageSize()) {
        return uploadFile(content, fileName);
      } else {
        throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
      }
    }
  }

  public String uploadFile(BufferedImage bufferedImage, String fileName, String extensions) {
    String fileUrl = null;
    File file = new File(fileName);
    try {
      ImageIO.write(bufferedImage, extensions, file);
      if (file.length() > appProperties.getMaxImageSize()) {
        file.delete();
        throw new CodeException(ErrorCode.INVALID_IMAGE_SIZE);
      }

      fileUrl =
          amazonS3Config.getEndpointUrl() + "/" + amazonS3Config.getBucketName() + "/" + fileName;
      uploadFileToS3Bucket(fileName, file);
      file.delete();
    } catch (IOException e) {
      log.error("", e);
    }
    return fileUrl;
  }


  public String uploadFile(String base64Image, String fileName) {
    String fileUrl = null;

    File file = null;
    try {
      file = convertBase64ToImage(base64Image);
      fileUrl =
          amazonS3Config.getEndpointUrl() + "/" + amazonS3Config.getBucketName() + "/" + fileName;
      uploadFileToS3Bucket(fileName, file);
      file.delete();
    } catch (IOException e) {
      log.error("", e);
    }

    return fileUrl;
  }

  public void deleteFile(String urlStr) {
    if (StringUtils.isNotBlank(urlStr)) {
      URL url = null;
      try {
        url = new URL(urlStr);
        String path = url.getPath();
        String key = path.substring(path.lastIndexOf("/"));
        amazonS3.deleteObject(new DeleteObjectRequest(amazonS3Config.getBucketName(), key));
      } catch (MalformedURLException e) {
        log.error("", e);
      }

    }
  }

  private void uploadFileToS3Bucket(String fileName, File file) {
    amazonS3.putObject(new PutObjectRequest(amazonS3Config.getBucketName(), fileName, file)
        .withCannedAcl(CannedAccessControlList.PublicRead));
  }

}
