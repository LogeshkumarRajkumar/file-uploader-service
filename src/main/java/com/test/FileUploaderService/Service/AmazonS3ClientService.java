package com.test.FileUploaderService.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.test.FileUploaderService.Utils.FileUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AmazonS3ClientService{

    private AmazonS3 s3client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private static final Logger LOGGER = Logger.getLogger(AmazonS3ClientService.class.getName());


    @PostConstruct
    private void initializeAmazon() {
        this.s3client = AmazonS3ClientBuilder.standard()
                .withRegion("eu-west-1")
                .build();
    }

    public String uploadFile(MultipartFile multipartFile) {
        try {
            File file = FileUtils.convertMultiPartToFile(multipartFile);
            String fileName = FileUtils.generateFileName(multipartFile, UUID.randomUUID().toString());
            s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                    .withCannedAcl(CannedAccessControlList.AuthenticatedRead));
            file.delete();
            return fileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occur", e);
            return null;
        }
    }

}