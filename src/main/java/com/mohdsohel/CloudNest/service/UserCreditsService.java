package com.mohdsohel.CloudNest.service;

import com.mohdsohel.CloudNest.document.UserCredits;

public interface UserCreditsService {

    UserCredits createInitialCredits(String clerkId);
    UserCredits getUserCredits(String clerkId);
    UserCredits getUserCredits();
    Boolean haveEnoughCredits(int requiredCredits);
    UserCredits consumeCredit();
}
