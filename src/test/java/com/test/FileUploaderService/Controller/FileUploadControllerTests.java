package com.test.FileUploaderService.Controller;

import com.test.FileUploaderService.Responses.FileCreatedResponse;
import com.test.FileUploaderService.Service.AmazonS3ClientService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import javax.annotation.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FileUploadControllerTests {

    @Mock
    AmazonS3ClientService s3ClientService;

    @InjectMocks
    @Resource
    FileUploadController controller;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadFileShouldReturnBadRequestIfFileIsInvalid(){

        //Given
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        when(s3ClientService.uploadFile(file)).thenReturn(null);

        //When
        ResponseEntity<FileCreatedResponse> response = controller.uploadFile(file);

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void uploadFileShouldReturnCreatedIfFileIsValid(){

        //Given
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        String fileId = "file-id";
        when(s3ClientService.uploadFile(file)).thenReturn(fileId);

        //When
        ResponseEntity<FileCreatedResponse> response = controller.uploadFile(file);

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.CREATED));
        assertTrue(response.getBody().id.equals(fileId));
    }

}
