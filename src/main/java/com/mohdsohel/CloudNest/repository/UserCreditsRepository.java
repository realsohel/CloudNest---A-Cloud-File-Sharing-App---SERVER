package com.mohdsohel.CloudNest.repository;

import com.mohdsohel.CloudNest.document.UserCredits;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCreditsRepository extends MongoRepository<UserCredits, String> {
}
