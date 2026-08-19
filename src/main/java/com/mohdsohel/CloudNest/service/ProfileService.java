package com.mohdsohel.CloudNest.service;

import com.mohdsohel.CloudNest.document.ProfileDocument;
import com.mohdsohel.CloudNest.dto.ProfileDto;

public interface ProfileService {

    ProfileDto createProfile(ProfileDto profileDto);

    ProfileDto updateProfile(ProfileDto profileDto);

    void deleteProfile(String clerkId);

    boolean existsByClerkId(String clerkId);

    ProfileDocument getProfile();
}
