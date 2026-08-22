package com.mohdsohel.CloudNest.service;

import com.mohdsohel.CloudNest.document.UserCredits;
import com.mohdsohel.CloudNest.document.enums.Plans;

public interface UserCreditsService {

    UserCredits createInitialCredits(String clerkId);
    UserCredits getUserCredits(String clerkId);
    UserCredits getUserCredits();
    Boolean haveEnoughCredits(int requiredCredits);
    UserCredits consumeCredit();
    UserCredits addCredits(String clerkId, Integer creditsToAdd, Plans plans);
}
