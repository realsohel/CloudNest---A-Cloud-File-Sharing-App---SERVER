package com.mohdsohel.CloudNest.service.impl;

import com.mohdsohel.CloudNest.document.FileMetadataDocument;
import com.mohdsohel.CloudNest.document.ProfileDocument;
import com.mohdsohel.CloudNest.dto.FileDto;
import com.mohdsohel.CloudNest.exceptions.FileStorageException;
import com.mohdsohel.CloudNest.exceptions.NotEnoughCreditsException;
import com.mohdsohel.CloudNest.exceptions.ResourceNotFoundException;
import com.mohdsohel.CloudNest.repository.FileMetadataRepository;
import com.mohdsohel.CloudNest.service.FileMetadataService;
import com.mohdsohel.CloudNest.service.ProfileService;
import com.mohdsohel.CloudNest.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;
    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;
    private final ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public List<FileDto> uploadFiles(MultipartFile[] files) throws IOException {
        ProfileDocument profileDocument = profileService.getProfile();
        List<FileMetadataDocument> savedFiles = new ArrayList<>();

        if(!userCreditsService.haveEnoughCredits((files.length))){
            throw new NotEnoughCreditsException("Not enough credits to upload files.");
        }

        Path uploadPth = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPth);
        List<Path> copiedFiles = new ArrayList<>();
        try{
            for(MultipartFile file: files){
                String fileName = UUID.randomUUID()+"."+ StringUtils.getFilenameExtension(file.getOriginalFilename());
                Path targetLocation = uploadPth.resolve(fileName);
                copiedFiles.add(targetLocation);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                FileMetadataDocument fileMetadataDocument = FileMetadataDocument.builder()
                        .fileLocation(targetLocation.toString())
                        .name(file.getOriginalFilename())
                        .size(file.getSize())
                        .type(file.getContentType())
                        .clerkId(profileDocument.getClerkId())
                        .isPublic(false)
                        .uploadedAt(LocalDateTime.now())
                        .build();

                userCreditsService.consumeCredit();
                savedFiles.add(fileMetadataRepository.save(fileMetadataDocument));
            }
        }
        catch (Exception e){

            for (Path path : copiedFiles) {
                Files.deleteIfExists(path);
            }
            throw e;
        }

        return savedFiles.stream()
                .map(fileMetadataDocument -> modelMapper.map(fileMetadataDocument,FileDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> getAllFiles() {
        ProfileDocument currentProfile = profileService.getProfile();

        List<FileMetadataDocument>  files = fileMetadataRepository.findByClerkId(currentProfile.getClerkId());

        return files.stream()
                .map(file-> modelMapper.map(file,FileDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public FileDto getPublicFile(String fileId) {
        FileMetadataDocument file = fileMetadataRepository.findById(fileId).orElseThrow(
                ()-> new ResourceNotFoundException("File not found")
        );

        if(!file.getIsPublic()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access of File");
        }

        return modelMapper.map(file,FileDto.class);

    }

    @Override
    public FileDto getDownloadableFile(String fileId) {

        FileDto file = getPublicFile(fileId);

        Path path = Paths.get(file.getFileLocation());

        if (!Files.exists(path)) {
            throw new FileStorageException(
                    "The requested file is no longer available."
            );
        }

        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new FileStorageException(
                    "The requested file cannot be accessed."
            );
        }

        return file;
    }

    @Override
    public void deleteFile(String fileId) {
        try {
            ProfileDocument userProfile = profileService.getProfile();
            FileMetadataDocument file = fileMetadataRepository.findById(fileId).orElseThrow(
                    ()-> new ResourceNotFoundException("File not found")
            );

            if(!userProfile.getClerkId().equals(file.getClerkId())){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access of File");
            }

            Path path = Paths.get(file.getFileLocation());
            Files.deleteIfExists(path);
            fileMetadataRepository.deleteById(fileId);

        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error, Please Again Later.");
        }
    }

    @Override
    public FileDto toggleFileVisibility(String fileId) {
        FileMetadataDocument file = fileMetadataRepository.findById(fileId).orElseThrow(
                ()-> new ResourceNotFoundException("File not found")
        );

        file.setIsPublic(!file.getIsPublic());

        return modelMapper.map(fileMetadataRepository.save(file), FileDto.class);
    }
}
