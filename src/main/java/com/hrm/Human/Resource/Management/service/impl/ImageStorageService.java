package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.service.IStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

@Service // day la  1 service
public class ImageStorageService implements IStorageService {
    private final Path storageFolder = Paths.get("uploads");  // thuoc tinh tham chieu den upload image
    // constructor
    public ImageStorageService() {
        try {
            Files.createDirectories(storageFolder);
        } catch (IOException exception) {
            throw new RuntimeException("Cannot initialize storage", exception);
        }
    }
    private boolean isAllowFile(MultipartFile file) {
        // let install FileNameUtils
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        assert fileExtension != null;
        return Arrays.asList(new String[] {"png", "jpeg", "jpg", "jpe", "bmp", "pdf", "docx", "pptx"})
                .contains(fileExtension.trim().toLowerCase()); // kiem tra duoi
    }

    private float getMaxFileSize(String fileExtension) {
        return switch (fileExtension.trim().toLowerCase()) {
            case "pdf" -> 10.0f;
            case "docx", "pptx" -> 15.0f;
            default -> 5.0f;
        };
    }
    @Override
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            // check file is image
            if (!isAllowFile(file)) {
                throw new RuntimeException("You can only upload certain types of file.");
            }
            assert fileExtension != null;
            float maxFileSize = getMaxFileSize(fileExtension);
            float fileSizeInMegabytes = file.getSize() / 1_000_000.0f;
            if (fileSizeInMegabytes > maxFileSize) {
                throw new RuntimeException("File must be <= " + maxFileSize + "MB");
            }

            // file must be rename ? why? khi upload 2 file co ten giong nhau 1 file cu xoa be ghi de roi xoa mat | quan trong
            String generatedFileName = UUID.randomUUID().toString() .replace("-", "");
            generatedFileName = generatedFileName + "." + fileExtension;
            Path destinationFilePath = this.storageFolder.resolve(
                    Paths.get(generatedFileName)).normalize().toAbsolutePath();
            if (!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath())) {
                throw new RuntimeException("Can not store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return generatedFileName;
        }
        catch (IOException exception) {
            throw new RuntimeException("Failed to store files.", exception);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try{
            return Files.walk(this.storageFolder, 1)
                    .filter(path -> !path.equals(this.storageFolder))
                    .map(this.storageFolder::relativize);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to loaf stores files", exception);
        }
    }


    @Override
    public byte[] readFileContent(String fileName) {
        try {
            Path file = storageFolder.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return StreamUtils.copyToByteArray(resource.getInputStream());
            }
            else {
                throw new RuntimeException(
                        "Could not read file: " + fileName
                );
            }
        }
        catch (IOException exception) {
            throw new RuntimeException("Could not read file: " + fileName, exception);
        }
    }

    @Override
    public void deleteAllFiles() {

    }

    @Override
    public String getContentType(String fileName) throws IOException {
        return Files.probeContentType(Paths.get(this.storageFolder.toString(), fileName));
    }


}