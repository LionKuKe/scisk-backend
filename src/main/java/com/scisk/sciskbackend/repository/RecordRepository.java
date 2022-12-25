package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.RecordDS;
import com.scisk.sciskbackend.datasourceentity.StepDS;
import com.scisk.sciskbackend.entity.Document;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends MongoRepository<RecordDS, Long> {

    @Aggregation(
            pipeline = {
                    "{$match: {_id: ?0}}",
                    "{$lookup: {from: 'service', localField: 'serviceId', foreignField: '_id', as: 'service'}}",
                    "{$unwind: {path: '$service', preserveNullAndEmptyArrays: true}}"
            }
    )
    Optional<RecordDS> findById(Long id);

    List<RecordDS> findAllByCreatedOnBetween(Instant start, Instant end);

    List<RecordDS> findAllByCode(String code);
}
