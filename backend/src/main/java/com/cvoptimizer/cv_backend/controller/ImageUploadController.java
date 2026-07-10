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
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/image-upload")
public class ImageUploadController {

    private static final String IMAGE_UPLOAD_DIR = "uploads/images/";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    @Autowired
    private OcrService ocrService;

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("File exceeds maximum size of 10MB");
        }

        String originalName = file.getOriginalFilename();
        String extension = (originalName != null)
                ? StringUtils.getFilenameExtension(originalName.toLowerCase())
                : null;

        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension)) {
            return ResponseEntity.badRequest().body("Invalid file type. Allowed: jpg, png, gif, bmp, tiff");
        }

        try {
            Path uploadDir = Paths.get(IMAGE_UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            // Use a UUID filename — never trust user-supplied filenames
            String filename = UUID.randomUUID() + "." + extension;
            Path target = uploadDir.resolve(filename).normalize();

            // Guard against path traversal
            if (!target.startsWith(uploadDir)) {
                return ResponseEntity.badRequest().body("Invalid file path");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            try {
                File savedImage = target.toFile();
                String extractedText = ocrService.extractTextFromImage(savedImage);
                return ResponseEntity.ok(extractedText);
            } finally {
                Files.deleteIfExists(target);
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Image upload failed");
        }
    }
}
