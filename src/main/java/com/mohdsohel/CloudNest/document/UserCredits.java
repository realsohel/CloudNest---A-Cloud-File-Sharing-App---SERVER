package com.mohdsohel.CloudNest.document;

import com.mohdsohel.CloudNest.document.enums.Plans;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_credits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredits {

    @Id
    private String id;
    private String clerkId;
    private Integer credits;
    private Plans plans;

}
