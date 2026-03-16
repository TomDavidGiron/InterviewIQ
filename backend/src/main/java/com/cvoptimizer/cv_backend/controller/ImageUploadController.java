package com.cvoptimizer.cv_backend.controller;

import com.cvoptimizer.cv_backend.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/image-upload")
@CrossOrigin(origins = "*")
public class ImageUploadController {

    private static final String IMAGE_UPLOAD_DIR = "uploads/images/";

    @Autowired
    private OcrService ocrService;

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(IMAGE_UPLOAD_DIR));
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String baseName = StringUtils.stripFilenameExtension(file.getOriginalFilename());
            String filename = baseName + "_" + System.currentTimeMillis() + "." + extension;

            Path path = Paths.get(IMAGE_UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            File savedImage = path.toFile();
            String extractedText = ocrService.extractTextFromImage(savedImage);

            return ResponseEntity.ok(extractedText);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Image upload failed");
        }
    }
}
