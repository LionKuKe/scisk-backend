package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.NeededDocumentDS;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NeededDocumentRepository extends MongoRepository<NeededDocumentDS, Long> {
    @Aggregation(
            pipeline = {
                    "{$match: {_id: ?0}}",
                    "{$lookup: {from: 'service', localField: 'serviceId', foreignField: '_id', as: 'service'}}",
                    "{$unwind: {path: '$service', preserveNullAndEmptyArrays: true}}"
            }
    )
    Optional<NeededDocumentDS> findById(Long id);
}
