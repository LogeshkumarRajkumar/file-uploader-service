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

    @Test
    public void getFileShouldReturnNotFoundWhenInvalidIdIsRequested() throws FileNotFoundException {

        //Given
        String fileId = "file-id";
        when(s3ClientService.getFile(fileId)).thenReturn(null);

        //When
        ResponseEntity response = controller.getFile(fileId);

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getFileShouldReturnRequestedFile() throws FileNotFoundException {

        //Given
        InputStreamResource res = new InputStreamResource(new FileInputStream(new File("./src/test/test")));
        String fileId = "file-id";
        when(s3ClientService.getFile(fileId)).thenReturn(res);

        //When
        ResponseEntity response = controller.getFile(fileId);

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.OK));
        assertTrue(response.getBody().equals(res));
    }

    @Test
    public void getAllFileShouldReturnNotFoundIfAnyException() throws FileNotFoundException {

        //Given
        when(s3ClientService.getAllFiles()).thenReturn(null);

        //When
        ResponseEntity response = controller.getAllFiles();

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.NOT_FOUND));
    }

    @Test
    public void getAllFilesShouldReturnAllFilesInAZip() throws FileNotFoundException {

        //Given
        InputStreamResource res = new InputStreamResource(new FileInputStream(new File("./src/test/test")));
        String fileId = "file-id";
        when(s3ClientService.getAllFiles()).thenReturn(res);

        //When
        ResponseEntity response = controller.getAllFiles();

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.OK));
        assertTrue(response.getBody().equals(res));
    }

    @Test
    public void deleteFileShouldReturnNotFound() throws FileNotFoundException {

        //Given
        String fileId = "file-id";
        when(s3ClientService.deleteFile(fileId)).thenReturn(false);

        //When
        ResponseEntity response = controller.deleteFile(fileId);

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteFileShouldDeleteTheFile() throws FileNotFoundException {

        //Given
        String fileId = "file-id";
        when(s3ClientService.deleteFile(fileId)).thenReturn(true);

        //When
        ResponseEntity response = controller.deleteFile(fileId);

        //Then
        assertTrue(response.getStatusCode().equals(HttpStatus.NO_CONTENT));
    }
}
