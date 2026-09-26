package com.example.azureblobproject.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.example.azureblobproject.service.BlobStorageService;

@RestController
@RequestMapping("/files")
public class BlobController {

    private final BlobStorageService blobStorageService;

    public BlobController(BlobStorageService blobStorageService) {
        this.blobStorageService = blobStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("department") String department,
            @RequestParam("subject") String subject,
            @RequestParam("year") String year,
            @RequestParam("type") String type) {

        try {
            String result = blobStorageService.uploadFile(
                    file, department, subject, year, type);

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<String>> listFiles() {

        return ResponseEntity.ok(
                blobStorageService.listFiles());
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchByTag(
            @RequestParam String tagName,
            @RequestParam String tagValue) {

        return ResponseEntity.ok(
                blobStorageService.searchByTag(tagName, tagValue));
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String fileName) {

        BlobClient blobClient =
                blobStorageService.getBlob(fileName);

        if (!blobClient.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = blobClient.downloadContent().toBytes();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @DeleteMapping("/{fileName:.+}")
    public ResponseEntity<String> deleteFile(
            @PathVariable String fileName) {

        return ResponseEntity.ok(
                blobStorageService.deleteFile(fileName));
    }
}