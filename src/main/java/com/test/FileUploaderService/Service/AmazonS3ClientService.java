package com.test.FileUploaderService.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.*;
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
    private static final String DESTINATION_DIRECTORY = "./Files";
    private static final String ZIP_LOCATION = "./Files.zip";


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

    public boolean deleteFile(String fileName) {
        try{
            s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occur", e);
            return false;
        }
    }

    public InputStreamResource getFile(String fileName){
        try {
            S3Object object = s3client.getObject(bucketName, fileName);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return new InputStreamResource(objectContent);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occur", e);
            return null;
        }
    }

    public InputStreamResource getAllFiles(){
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3client).build();
        try {
            File fileTransferPath = new File(DESTINATION_DIRECTORY);
            MultipleFileDownload download = xfer_mgr.downloadDirectory(bucketName, null, fileTransferPath);
            download.waitForCompletion();

            return getTransferableData(fileTransferPath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occur", e);
        }
        xfer_mgr.shutdownNow();
        return null;
    }

    private InputStreamResource getTransferableData(File destination){
        try{
            FileUtils.zipFolder(Paths.get(DESTINATION_DIRECTORY), Paths.get(ZIP_LOCATION));
            FileUtils.deleteDirectory(destination);

            File zipFile = new File(ZIP_LOCATION);
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(zipFile));
            zipFile.delete();
            return inputStreamResource;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occur", e);
            return null;
        }
    }
}