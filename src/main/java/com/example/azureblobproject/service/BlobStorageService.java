package com.example.azureblobproject.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.TaggedBlobItem;

@Service
public class BlobStorageService {

    private final BlobContainerClient containerClient;

    public BlobStorageService(
            BlobServiceClient blobServiceClient,
            @Value("${azure.storage.container-name}") String containerName) {

        this.containerClient =
                blobServiceClient.getBlobContainerClient(containerName);

        if (!containerClient.exists()) {
            containerClient.create();
        }
    }

    // Upload file with Blob Index Tags
    public String uploadFile(
            MultipartFile file,
            String department,
            String subject,
            String year,
            String type) throws IOException {

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        BlobClient blobClient =
                containerClient.getBlobClient(fileName);

        try (InputStream inputStream = file.getInputStream()) {

            blobClient.upload(inputStream, file.getSize(), true);

            Map<String, String> tags = new HashMap<>();

            tags.put("Department", department);
            tags.put("Subject", subject);
            tags.put("Year", year);
            tags.put("Type", type);

            blobClient.setTags(tags);

            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType(file.getContentType());

            blobClient.setHttpHeaders(headers);
        }

        return "File uploaded successfully: " + fileName;
    }

    // List all files
    public List<String> listFiles() {

        List<String> files = new ArrayList<>();

        for (BlobItem blobItem : containerClient.listBlobs()) {
            files.add(blobItem.getName());
        }

        return files;
    }

    // Search files using Blob Index Tags
    public List<String> searchByTag(
            String tagName,
            String tagValue) {

        List<String> matchingFiles = new ArrayList<>();

        String query = "\"" + tagName + "\"='" + tagValue + "'";

        for (TaggedBlobItem blobItem :
                containerClient.findBlobsByTags(query)) {

            matchingFiles.add(blobItem.getName());
        }

        return matchingFiles;
    }

    // Get blob for downloading
    public BlobClient getBlob(String fileName) {

        return containerClient.getBlobClient(fileName);
    }

    // Delete file
    public String deleteFile(String fileName) {

        BlobClient blobClient =
                containerClient.getBlobClient(fileName);

        if (!blobClient.exists()) {
            return "File not found: " + fileName;
        }

        blobClient.delete();

        return "File deleted successfully: " + fileName;
    }

    // Bulk upload files from a local folder with Blob Index Tags
    public String bulkUploadFromFolder(String folderPath) throws IOException {

        Path folder = Paths.get(folderPath);

        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            return "Folder not found: " + folderPath;
        }

        String[] departments = {
                "CSE",
                "ECE",
                "EEE",
                "MECH"
        };

        String[] subjects = {
                "Java",
                "Python",
                "Cloud",
                "DBMS",
                "AI"
        };

        String[] types = {
                "Notes",
                "Assignment",
                "LabRecord",
                "QuestionPaper"
        };

        int count = 0;

        try (var files = Files.list(folder)) {

            for (Path path :
                    files.filter(Files::isRegularFile).toList()) {

                String fileName =
                        path.getFileName().toString();

                BlobClient blobClient =
                        containerClient.getBlobClient(fileName);

                byte[] data =
                        Files.readAllBytes(path);

                blobClient.upload(
                        new java.io.ByteArrayInputStream(data),
                        data.length,
                        true);

                String numberText =
                        fileName
                                .replace("document_", "")
                                .replace(".txt", "");

                int number =
                        Integer.parseInt(numberText);

                String department =
                        departments[
                                (number - 1)
                                % departments.length
                        ];

                String subject =
                        subjects[
                                (number - 1)
                                % subjects.length
                        ];

                String type =
                        types[
                                (number - 1)
                                % types.length
                        ];

                Map<String, String> tags =
                        new HashMap<>();

                tags.put("Department", department);
                tags.put("Subject", subject);
                tags.put("Year", "2026");
                tags.put("Type", type);

                blobClient.setTags(tags);

                blobClient.setHttpHeaders(
                        new BlobHttpHeaders()
                                .setContentType("text/plain")
                );

                count++;
            }
        }

        return count
                + " files uploaded successfully with Blob Index Tags.";
    }
}