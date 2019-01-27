package com.test.FileUploaderService.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AmazonS3ClientServiceTests {
    AmazonS3 client = Mockito.mock(AmazonS3.class);
    TransferManager transferManager = Mockito.mock(TransferManager.class);

    @InjectMocks
    AmazonS3ClientService s3ClientService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadFilesShouldUploadFileToS3() throws IOException {
        // Given
        MockMultipartFile multipartFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        when(client.putObject(any())).thenReturn(new PutObjectResult());

        // When
        String result = s3ClientService.uploadFile(multipartFile);

        // Then
        assertTrue(result != null);
    }

    @Test
    public void uploadFilesShouldReturnNullWhenAnyExceptionThrown() throws Exception {
        // Given
        SdkClientException sdkClientException = new SdkClientException("");
        when(client.putObject(any())).thenThrow(sdkClientException);
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        // When
        String result = s3ClientService.uploadFile(file);

        // Then
        assertTrue(result == null);
    }

    @Test
    public void getFileShouldReturnRequestedFile() throws Exception {

        // Given
        S3Object obj = new S3Object();
        obj.setObjectContent(new FileInputStream(new File("./src/test/test")));
        when(client.getObject(any(), (String) any())).thenReturn(obj);
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        // When
        InputStreamResource result = s3ClientService.getFile("file");

        // Then
        assertTrue(result != null);
        InputStreamResource expected = new InputStreamResource(obj.getObjectContent());
        assertTrue(expected.equals(result));

    }

    @Test
    public void getFileShouldReturnNullWhenAnyExceptionThrown() throws Exception {
        // Given
        SdkClientException sdkClientException = new SdkClientException("");
        when(client.getObject(any(), (String) any())).thenThrow(sdkClientException);
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        // When
        InputStreamResource result = s3ClientService.getFile("file");

        // Then
        assertTrue(result == null);
    }
}
