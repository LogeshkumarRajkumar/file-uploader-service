package com.test.FileUploaderService.Utils;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static junit.framework.TestCase.assertEquals;

public class FileUtilsTests {

    @Test
    public void shouldConvertMultiPartFileToFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        File convertedFile = FileUtils.convertMultiPartToFile(file);

        for(int i=0; i <Files.readAllBytes(convertedFile.toPath()).length; i++) {
            assertEquals(0, Byte.compare(file.getBytes()[i], Files.readAllBytes(convertedFile.toPath())[i]));
        }
        File toDelete1 = new File("./filename.txt");
        File toDelete2 = new File("./data");
        toDelete1.delete();
        toDelete2.delete();
    }
}
