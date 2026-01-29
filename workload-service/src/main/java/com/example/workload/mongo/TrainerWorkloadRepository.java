package com.example.workload.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkloadDocument, String> {
    // username = @Id, so default findById/save cover the requirement
}
