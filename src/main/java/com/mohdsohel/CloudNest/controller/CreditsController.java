package com.mohdsohel.CloudNest.controller;

import com.mohdsohel.CloudNest.document.UserCredits;
import com.mohdsohel.CloudNest.dto.UserCreditsDto;
import com.mohdsohel.CloudNest.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/credits")
public class CreditsController {

    private final UserCreditsService creditsService;

    @GetMapping("/get-credits")
    public ResponseEntity<?> getUserCredits(){
        UserCredits userCredits = creditsService.getUserCredits();
        UserCreditsDto userCreditsDto = UserCreditsDto.builder()
                .credits(userCredits.getCredits())
                .plans(userCredits.getPlans())
                .build();
        return ResponseEntity.ok().body(userCreditsDto);
    }
}
