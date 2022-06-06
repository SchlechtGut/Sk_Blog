package com.example.sk_blog.service;

import com.example.sk_blog.api.response.TrueOrErrorsResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ResourceStorage {

    @Value("${upload.path}")
    String uploadPath;

    public ResponseEntity<?> saveNewBookImage(MultipartFile file) throws IOException {
        Map<String, String> errors = new LinkedHashMap<>();

//        if (file.getSize() > 1024 * 1024) {
//            errors.put("image", "Размер файла превышает допустимый размер");
//            return ResponseEntity.badRequest().body(new TrueOrErrorsResponse(errors));
//        }

        if (!file.isEmpty()) {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());

            if (!extension.equals("png") && !extension.equals("jpg")) {
                errors.put("image", "изображение должно быть png или jpg");
                return ResponseEntity.badRequest().body(new TrueOrErrorsResponse(errors));
            }

            uploadPath += RandomStringUtils.randomAlphanumeric(2)
                    + File.separatorChar
                    + RandomStringUtils.randomAlphanumeric(2)
                    + File.separatorChar
                    + RandomStringUtils.randomAlphanumeric(2);

            if (!new File(uploadPath).exists()) {
                Files.createDirectories(Paths.get(uploadPath));
            }

            String fileName = RandomStringUtils.randomAlphanumeric(5) + "." + extension;
            Path path = Paths.get(uploadPath, fileName);

            file.transferTo(path);
            Logger.getLogger(this.getClass().getSimpleName()).info( "file was uploaded in " + path);

            return ResponseEntity.ok(path.toString());
        }

        return ResponseEntity.badRequest().body(new TrueOrErrorsResponse(errors));
    }
}
