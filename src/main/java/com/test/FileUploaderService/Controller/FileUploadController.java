package com.test.FileUploaderService.Controller;

import com.test.FileUploaderService.Responses.FileCreatedResponse;
import com.test.FileUploaderService.Service.AmazonS3ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/v1")
public class FileUploadController {

    @Autowired
    private AmazonS3ClientService amazonS3ClientService;

    @PostMapping("/files")
    public ResponseEntity<FileCreatedResponse> uploadFile(@RequestPart(value = "file") MultipartFile file) {
        String uniqueFileIdentifier = amazonS3ClientService.uploadFile(file);

        if(uniqueFileIdentifier!=null)
            return new ResponseEntity<FileCreatedResponse>(new FileCreatedResponse(uniqueFileIdentifier),HttpStatus.CREATED);
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }
}
