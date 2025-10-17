package sasha.org.edusmart.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sasha.org.edusmart.service.FileResourceService;

import java.io.IOException;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class FileResourceController {

    private final FileResourceService fileResourceService;

    public FileResourceController(FileResourceService fileResourceService) {
        this.fileResourceService = fileResourceService;
    }

    @PostMapping("/files/upload/{courseTitle}")
    public ResponseEntity<String> uploadFile(@PathVariable String courseTitle, @RequestParam("file") MultipartFile file) {

        boolean success = fileResourceService.uploadFileOnCourse(courseTitle, file);
        if (success) {
            return ResponseEntity.ok("File uploaded and saved successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Upload failed. Course not found or I/O error.");
        }
    }

    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileId) {
        try {
            Resource resource = fileResourceService.downloadFile(fileId);

            String filename = Paths.get(resource.getFilename()).getFileName().toString();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
