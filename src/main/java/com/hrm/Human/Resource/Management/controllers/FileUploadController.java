    package com.hrm.Human.Resource.Management.controllers;

    import com.hrm.Human.Resource.Management.response.EmployeeResponse;
    import com.hrm.Human.Resource.Management.service.IStorageService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

    import java.util.List;
    import java.util.stream.Collectors;

    @Controller
    @RequestMapping(path = "/api/FileUpload")
    public class FileUploadController {
        // inject storage service here
        @Autowired
        private IStorageService storageService;
        // this controller receive file/ image from client
        @PostMapping("") // search
        public ResponseEntity<EmployeeResponse> uploadFile(@RequestParam("file")MultipartFile file) {
            try {
                // save files to a folder => use a service
                String generatedFileName = storageService.storeFile(file);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new EmployeeResponse("ok", "upload successfully", generatedFileName)

                );
            } catch (Exception exception) {
                return  ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body (
                        new EmployeeResponse("ok", exception.getMessage(), "")
                );
            }
        }
        @GetMapping("/files/{fileName:.+}")
        public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName) {
            try {
                byte[] bytes = storageService.readFileContent(fileName);
                String contentType = storageService.getContentType(fileName);
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(bytes);
            } catch (Exception exception) {
                return ResponseEntity.noContent().build();
            }
        }

        // load all uploaded files
        @GetMapping("")
        public ResponseEntity<EmployeeResponse> getUploadedFiles() {
            try {
                List<String> urls = storageService.loadAll()
                        .map(path -> {
                            //convert fileName to url ( send request "readDetailFile)
                            String urlsPath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                    "readDetailFile", path.getFileName().toString()).build().toUri().toString();
                            return urlsPath;
                        })
                        .collect(Collectors.toList());
                return ResponseEntity.ok(new EmployeeResponse("ok", "List files successfully", urls));
            }catch (Exception exception) {
                return ResponseEntity.ok(
                        new EmployeeResponse("failed", "List files failed", new String[] {})
                );
            }
        }
    }