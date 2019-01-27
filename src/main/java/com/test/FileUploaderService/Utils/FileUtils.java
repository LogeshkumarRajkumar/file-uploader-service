package com.test.FileUploaderService.Utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils extends org.apache.tomcat.util.http.fileupload.FileUtils {

    public static final String FILE_PREFIX = "File-";

    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getName());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public static String generateFileName(MultipartFile file, String uniqueId) {
        return FILE_PREFIX + file.getOriginalFilename().replace(" ", "") + uniqueId;
    }
}
