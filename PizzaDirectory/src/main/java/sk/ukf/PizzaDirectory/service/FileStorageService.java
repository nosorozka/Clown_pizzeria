package sk.ukf.PizzaDirectory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            System.out.println("Upload directory initialized at: " + uploadPath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    /**
     * Store uploaded file and return the filename
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generate unique filename
        String newFilename = UUID.randomUUID().toString() + extension;
        
        try {
            Path targetLocation = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File stored successfully: " + targetLocation.toString());
            return newFilename;
        } catch (IOException e) {
            System.err.println("Failed to store file: " + originalFilename);
            e.printStackTrace();
            throw new RuntimeException("Could not store file " + originalFilename, e);
        }
    }

    /**
     * Delete file by filename
     */
    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }
        try {
            Path filePath = uploadPath.resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but don't throw
            System.err.println("Could not delete file: " + filename);
        }
    }

    /**
     * Get full path to file
     */
    public Path getFilePath(String filename) {
        return uploadPath.resolve(filename);
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        return Files.exists(uploadPath.resolve(filename));
    }
}

