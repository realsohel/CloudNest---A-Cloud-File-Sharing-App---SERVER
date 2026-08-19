package com.mohdsohel.CloudNest.service.impl;

import com.mohdsohel.CloudNest.document.ProfileDocument;
import com.mohdsohel.CloudNest.dto.ProfileDto;
import com.mohdsohel.CloudNest.repository.ProfileRepository;
import com.mohdsohel.CloudNest.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ModelMapper modelMapper;
    private final ProfileRepository profileRepository;

    @Override
    public ProfileDto createProfile(ProfileDto profileDto) {

        ProfileDocument chkProfile = profileRepository.findByEmail(profileDto.getEmail()).orElse(null);

        if(profileRepository.existsByClerkId(profileDto.getClerkId())){
            return updateProfile(profileDto);
        }

        if(chkProfile !=null){
            throw new DuplicateKeyException("Profile already exists with email: " + profileDto.getEmail());
        }

        ProfileDocument profile = modelMapper.map(profileDto, ProfileDocument.class);


        profile.setCreatedAt(Instant.now());
        profile.setCredits(5);

        profile = profileRepository.save(profile);

        return modelMapper.map(profile, ProfileDto.class);
    }


    @Override
    public ProfileDto updateProfile(ProfileDto profileDto) {
        ProfileDocument existingProfile = profileRepository.findByClerkId(profileDto.getClerkId());

        if(existingProfile !=null){
            if(profileDto.getEmail()!= null && !profileDto.getEmail().isEmpty()){
                existingProfile.setEmail(profileDto.getEmail());
            }

            if(profileDto.getFirstName()!= null && !profileDto.getFirstName().isEmpty()){
                existingProfile.setFirstName(profileDto.getFirstName());
            }

            if(profileDto.getLastName()!= null && !profileDto.getLastName().isEmpty()){
                existingProfile.setEmail(profileDto.getLastName());
            }

            if(profileDto.getPhotoUrl()!= null && !profileDto.getPhotoUrl().isEmpty()){
                existingProfile.setEmail(profileDto.getPhotoUrl());
            }

            existingProfile.setUpdatedAt(Instant.now());
            existingProfile = profileRepository.save(existingProfile);
            return modelMapper.map(existingProfile, ProfileDto.class);
        }
        else{
            existingProfile = modelMapper.map(profileDto, ProfileDocument.class);
            existingProfile.setUpdatedAt(Instant.now());
            return modelMapper.map(profileRepository.save(existingProfile), ProfileDto.class);
        }

    }

    @Override
    public void deleteProfile(String clerkId) {
        ProfileDocument profileDocument = profileRepository.findByClerkId(clerkId);

        if(clerkId!=null){
            profileRepository.delete(profileDocument);
        }
    }

    @Override
    public boolean existsByClerkId(String clerkId) {
        return profileRepository.existsByClerkId(clerkId);
    }

    @Override
    public ProfileDocument getProfile() {

        if(SecurityContextHolder.getContext().getAuthentication()==null){
            throw new UsernameNotFoundException("User not authenticated");
        }

        System.out.println("Security Context: " + SecurityContextHolder.getContext().getAuthentication().getName());

        String clerkId = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByClerkId(clerkId);
    }

}
