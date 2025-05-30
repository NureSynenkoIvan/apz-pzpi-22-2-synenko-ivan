package com.bastionserver.analysis.service.database;

import com.bastionserver.analysis.model.SkyState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkyStateRepository extends MongoRepository<SkyState,String> {
}
