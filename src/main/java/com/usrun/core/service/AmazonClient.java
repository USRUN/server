package com.usrun.core.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usrun.core.config.AmazonS3Config;
import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

@Slf4j
@Service
public class AmazonClient {

    private AmazonS3 amazonS3;

    @Autowired
    private AmazonS3Config amazonS3Config;

    @PostConstruct
    private void init() {
        AWSCredentials credentials = new BasicAWSCredentials(amazonS3Config.getAccessKey(), amazonS3Config.getSecretKey());
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

    public String uploadFile(String base64Image, String fileName) {
        String fileUrl = null;

        try {
            File file = convertBase64ToImage(base64Image);
            fileUrl = amazonS3Config.getEndpointUrl() + "/" + amazonS3Config.getBucketName() + "/" + fileName;
            uploadFileToS3Bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            log.error("", e);
        }
        return fileUrl;
    }

    private void uploadFileToS3Bucket(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(amazonS3Config.getBucketName(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

}
