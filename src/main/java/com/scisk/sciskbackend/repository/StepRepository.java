package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.StepDS;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StepRepository extends MongoRepository<StepDS, Long> {
    @Aggregation(
            pipeline = {
                    "{$match: {_id: ?0}}",
                    "{$lookup: {from: 'service', localField: 'serviceId', foreignField: '_id', as: 'service'}}",
                    "{$unwind: {path: '$service', preserveNullAndEmptyArrays: true}}"
            }
    )
    Optional<StepDS> findById(Long id);
}
