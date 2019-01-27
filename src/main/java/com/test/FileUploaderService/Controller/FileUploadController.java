package com.test.FileUploaderService.Controller;

import com.test.FileUploaderService.Responses.FileCreatedResponse;
import com.test.FileUploaderService.Service.AmazonS3ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @GetMapping("/files/{name}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("name") String fileName) {
        InputStreamResource file = amazonS3ClientService.getFile(fileName);

        if(file == null)
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .body(file);
    }

    @GetMapping("/files/")
    public ResponseEntity<InputStreamResource> getAllFiles() {
        InputStreamResource file = amazonS3ClientService.getAllFiles();

        if(file == null)
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                .header("Content-Disposition", "attachment; filename=AllFiles.Zip")
                .body(amazonS3ClientService.getAllFiles());
    }

    @DeleteMapping("/files/{name}")
    public ResponseEntity deleteFile(@PathVariable("name") String fileName) {

        if(this.amazonS3ClientService.deleteFile(fileName) == false)
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity(null, HttpStatus.NO_CONTENT);
    }
}
