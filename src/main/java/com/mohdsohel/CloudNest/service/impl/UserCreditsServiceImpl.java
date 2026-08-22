package com.mohdsohel.CloudNest.service.impl;

import com.mohdsohel.CloudNest.document.UserCredits;
import com.mohdsohel.CloudNest.document.enums.Plans;
import com.mohdsohel.CloudNest.repository.UserCreditsRepository;
import com.mohdsohel.CloudNest.service.ProfileService;
import com.mohdsohel.CloudNest.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreditsServiceImpl implements UserCreditsService {

    private final UserCreditsRepository userCreditsRepository;
    private final ProfileService profileService;

    public UserCredits createInitialCredits(String clerkId){
        UserCredits userCredits = UserCredits.builder()
                .clerkId(clerkId)
                .credits(5)
                .plans(Plans.BASIC)
                .build();

        return userCreditsRepository.save(userCredits);
    }

    public UserCredits getUserCredits(String clerkId){
        return userCreditsRepository.findByClerkId(clerkId)
                .orElseGet(()-> createInitialCredits(clerkId));
    }

    @Override
    public UserCredits getUserCredits() {
        String clerkId = profileService.getProfile().getClerkId();
        return getUserCredits(clerkId);
    }

    @Override
    public Boolean haveEnoughCredits(int requiredCredits) {
        UserCredits userCredits = getUserCredits();
        return userCredits.getCredits() >= requiredCredits;
    }

    @Override
    public UserCredits consumeCredit() {
        UserCredits userCredits = getUserCredits();

        if(userCredits.getCredits()<=0){
            return null;
        }

        userCredits.setCredits(userCredits.getCredits() - 1);
        return  userCreditsRepository.save(userCredits);
    }

    @Override
    public UserCredits addCredits(String clerkId, Integer creditsToAdd, Plans plans) {
        UserCredits userCredits = userCreditsRepository.findByClerkId(clerkId)
                .orElseGet(()-> createInitialCredits(clerkId));

        userCredits.setCredits(userCredits.getCredits() + creditsToAdd);
        userCredits.setPlans(plans);

        return userCreditsRepository.save(userCredits);
    }
}
