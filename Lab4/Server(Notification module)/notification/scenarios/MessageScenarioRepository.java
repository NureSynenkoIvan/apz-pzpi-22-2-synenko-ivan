package com.bastionserver.notification.scenarios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageScenarioRepository extends MongoRepository<MessageScenario, String> {
    Optional<MessageScenario> findByScenarioName(String scenarioName);
}
