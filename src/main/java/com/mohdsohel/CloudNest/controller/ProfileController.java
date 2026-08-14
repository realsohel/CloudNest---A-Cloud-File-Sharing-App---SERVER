package com.mohdsohel.CloudNest.controller;

import com.mohdsohel.CloudNest.dto.ProfileDto;
import com.mohdsohel.CloudNest.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> createProfile(@RequestBody ProfileDto profileDto){
        HttpStatus status = profileService.existsByClerkId(profileDto.getClerkId()) ? HttpStatus.OK : HttpStatus.CREATED;

        ProfileDto profile = profileService.createProfile(profileDto);
        return new ResponseEntity<>(profile, status);
    }

//    @DeleteMapping("/delete-profile/{clerkId}")
//    public ResponseEntity<> deleteProfile(@PathVariable String clerkId){
//        profileService.deleteProfile(clerkId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
