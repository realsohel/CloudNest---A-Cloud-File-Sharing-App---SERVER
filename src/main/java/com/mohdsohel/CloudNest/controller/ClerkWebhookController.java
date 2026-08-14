package com.mohdsohel.CloudNest.controller;

import com.mohdsohel.CloudNest.document.UserCredits;
import com.mohdsohel.CloudNest.dto.ProfileDto;
import com.mohdsohel.CloudNest.service.ProfileService;
import com.mohdsohel.CloudNest.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class ClerkWebhookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final ProfileService profile;

    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId,
                                                @RequestHeader("svix-timestamp") String svixTimeStamp,
                                                @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload) {

        try {
            boolean isValid = verifyWebhookSignature(svixId,svixTimeStamp,svixSignature,payload);

            if(!isValid){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid webhook signature");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);

            String eventType = jsonNode.path("type").asText();

            switch (eventType){
                case "user.created":
                    handleUserCreated(jsonNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(jsonNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(jsonNode.path("data"));
                    break;
            }

            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    private boolean verifyWebhookSignature(String svixId, String svixTimeStamp, String svixSignature, String payload){
//        TODO: Verify it Properly
        return true;
    }

    private void handleUserCreated(JsonNode data) {
        String clerkId = data.path("id").asText();
        System.out.println("========== CLERK USER CREATED ==========");
        System.out.println("Clerk ID: " + clerkId);
        String email="";
        JsonNode emailAddressess = data.path("email_addresses");

        if(emailAddressess.isArray() && emailAddressess.size()>0){
            email = emailAddressess.get(0).path("email_address").asText();
        }

        String firstName = data.path("first_name").asText(null);
        String lastName = data.path("last_name").asText(null);
        String photoUrl = data.path("image_url").asText(null);


        ProfileDto newProfile = new ProfileDto().builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(photoUrl)
                .build();
        System.out.println("Creating profile...");

        profileService.createProfile(newProfile);

        System.out.println("Profile created successfully.");
        System.out.println("Creating user credits...");

        UserCredits savedCredits = userCreditsService.createInitialCredits(clerkId);

        System.out.println("Credits created successfully.");
        System.out.println("Credits ID: " + savedCredits.getId());
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asText();

        String email="";
        JsonNode emailAddressess = data.path("email_addresses");

        if(emailAddressess.isArray() && emailAddressess.size()>0){
            email = emailAddressess.get(0).path("email_address").asText();
        }

        String firstName = data.path("first_name").asText(null);
        String lastName = data.path("last_name").asText(null);
        String photoUrl = data.path("image_url").asText(null);

        ProfileDto updateProfile = new ProfileDto().builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(photoUrl)
                .build();

        updateProfile = profileService.createProfile(updateProfile);

        if(updateProfile==null){
            handleUserCreated(data);
        }
    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();
        profileService.deleteProfile(clerkId);
    }
}
