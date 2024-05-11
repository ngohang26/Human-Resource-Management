package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.response.UploadResponse;
import com.hrm.Human.Resource.Management.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/api/FileUpload")
public class FileUploadController {
    @Autowired
    private IStorageService storageService;

    @PostMapping("") // search
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String generatedFileName = storageService.storeFile(file, "images");
            return ResponseEntity.status(HttpStatus.OK).body(
                  new UploadResponse("ok", "upload successfully", generatedFileName)

            );
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new UploadResponse("error", exception.getMessage(), "")
            );
        }
    }

    @PostMapping("/uploadResume")
    public ResponseEntity<UploadResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        String generatedFileName = storageService.storeResumeFile(file  , "resumes");
        return ResponseEntity.status(HttpStatus.OK).body(
                new UploadResponse("ok", "upload successfully", generatedFileName)
        );
    }

    @GetMapping("/files/images/{fileName:.+}")
    public ResponseEntity<byte[]> readImageFile(@PathVariable String fileName) {
        try {
            byte[] bytes = storageService.readImageFile(fileName);
            String contentType = storageService.getContentType("images/" + fileName);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);
        } catch (Exception exception) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/files/resumes/{fileName:.+}")
    public ResponseEntity<byte[]> readResumeFile(@PathVariable String fileName) {
        try {
            byte[] bytes = storageService.readResumeFile(fileName);
            String contentType = storageService.getContentType("resumes/" + fileName);
            String contentDisposition = "attachment; filename=\"" + fileName + "\"";

            if (contentType.equals("application/pdf")) {
                contentDisposition = "inline; filename=\"" + fileName + "\"";
            }

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(bytes);
        } catch (Exception exception) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<UploadResponse> getUploadedFiles() {
        try {
            List<String> urls = storageService.loadAll()
                    .map(path -> {
                        //convert fileName to url ( send request "readDetailFile)
                        String urlsPath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "readDetailFile", path.getFileName().toString()).build().toUri().toString();
                        return urlsPath;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new UploadResponse("ok", "List files successfully", urls));
        } catch (Exception exception) {
            return ResponseEntity.ok(
                    new UploadResponse("failed", "List files failed", new String[]{})
            );
        }
    }

    @PutMapping("/{fileName}")
    public ResponseEntity<UploadResponse> updateFile(@PathVariable String fileName, @RequestParam("file") MultipartFile file) {
        try {
            // Xóa hình ảnh cũ

            Files.deleteIfExists(storageService.getStorageFolder().resolve(fileName));
            // Lưu hình ảnh mới và lấy tên file được tạo
            String generatedFileName = storageService.storeFile(file, "images");

            return ResponseEntity.status(HttpStatus.OK).body(
                    new UploadResponse("ok", "update successfully", generatedFileName)
            );
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new UploadResponse("failed", exception.getMessage(), "")
            );
        }
    }

}