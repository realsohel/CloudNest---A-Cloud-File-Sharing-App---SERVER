package com.mohdsohel.CloudNest.controller;

import com.mohdsohel.CloudNest.document.UserCredits;
import com.mohdsohel.CloudNest.dto.FileDto;
import com.mohdsohel.CloudNest.exceptions.ResourceNotFoundException;
import com.mohdsohel.CloudNest.service.FileMetadataService;
import com.mohdsohel.CloudNest.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileMetadataService fileMetadataService;
    private final UserCreditsService userCreditsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files")MultipartFile files[]) throws Exception {
        Map<String,Object> response = new HashMap<>();
        List<FileDto> list = fileMetadataService.uploadFiles(files);
        UserCredits userCredits = userCreditsService.getUserCredits();

        response.put("files", list);
        response.put("remainingCredits",userCredits.getCredits());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-files")
    public ResponseEntity<?>  getAllFiles() {
        List<FileDto> list = fileMetadataService.getAllFiles();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/public/{fileId}")
    public ResponseEntity<?>  getPublicFile(@PathVariable String fileId) {
        FileDto file = fileMetadataService.getPublicFile(fileId);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileId) throws IOException {

        FileDto downloadableFile =
                fileMetadataService.getDownloadableFile(fileId);

        Path path = Paths.get(downloadableFile.getFileLocation())
                .toAbsolutePath()
                .normalize();

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                downloadableFile.getName() +
                                "\""
                )
                .body(resource);
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId){
        fileMetadataService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{fileId}/toggle-public")
    public ResponseEntity<?> toggleFileVisibility(@PathVariable String fileId){
        FileDto file = fileMetadataService.toggleFileVisibility(fileId);
        return ResponseEntity.ok(file);
    }
}
