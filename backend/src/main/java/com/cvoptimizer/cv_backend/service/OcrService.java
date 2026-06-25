package com.cvoptimizer.cv_backend.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService(@Value("${ocr.tessdata-path:}") String configuredDatapath) {
        tesseract = new Tesseract();
        tesseract.setDatapath(resolveDatapath(configuredDatapath));
        tesseract.setLanguage("eng");
    }

    public String extractTextFromImage(File imageFile) {
        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "OCR Error: " + e.getMessage();
        }
    }

    private String resolveDatapath(String configuredDatapath) {
        if (configuredDatapath != null && !configuredDatapath.isBlank()) {
            return configuredDatapath;
        }

        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return "C:\\Program Files\\Tesseract-OCR\\tessdata";
        }
        if (os.contains("mac")) {
            return "/opt/homebrew/share/tessdata";
        }
        return "/usr/share/tesseract-ocr/4.00/tessdata";
    }
}
