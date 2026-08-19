package com.mohdsohel.CloudNest.service;

import com.mohdsohel.CloudNest.dto.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileMetadataService {

    List<FileDto> uploadFiles(MultipartFile files[]) throws IOException;
    List<FileDto> getAllFiles();
    FileDto getPublicFile(String fileId);
    FileDto getDownloadableFile(String fileId);
    void deleteFile(String fileId);
    FileDto toggleFileVisibility(String fileId);
}
