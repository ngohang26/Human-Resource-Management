package com.hrm.Human.Resource.Management.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorageService {
    String storeFile(MultipartFile file, String subfolder);

    String storeResumeFile(MultipartFile file, String subfolder);

    public Stream<Path> loadAll();
    public void deleteAllFiles();
    public String getContentType(String fileName) throws IOException;
    Path getStorageFolder();

    byte[] readImageFile(String fileName);

    byte[] readResumeFile(String fileName);

    byte[] readFileContent(Path filePath);
}