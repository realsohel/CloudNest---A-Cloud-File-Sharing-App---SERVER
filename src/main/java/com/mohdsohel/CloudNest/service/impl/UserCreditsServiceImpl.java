package com.mohdsohel.CloudNest.service.impl;

import com.mohdsohel.CloudNest.document.UserCredits;
import com.mohdsohel.CloudNest.document.enums.Plans;
import com.mohdsohel.CloudNest.repository.UserCreditsRepository;
import com.mohdsohel.CloudNest.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditsServiceImpl implements UserCreditsService {

    private final UserCreditsRepository userCreditsRepository;

    public UserCredits createInitialCredits(String clerkId){
        UserCredits userCredits = UserCredits.builder()
                .clerkId(clerkId)
                .credits(5)
                .plans(Plans.BASIC)
                .build();

        return userCreditsRepository.save(userCredits);
    }
}
