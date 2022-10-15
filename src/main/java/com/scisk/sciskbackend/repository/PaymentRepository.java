package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.PaymentDS;
import com.scisk.sciskbackend.datasourceentity.StepDS;
import com.scisk.sciskbackend.entity.Payment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentDS, Long> {

    @Query("{recordId: ?0}")
    List<PaymentDS> findByRecordId(Long recordId);

    @Aggregation(
            pipeline = {
                    "{$match: {_id: ?0}}",
                    "{$lookup: {from: 'record', localField: 'recordId', foreignField: '_id', as: 'record'}}",
                    "{$unwind: {path: '$record', preserveNullAndEmptyArrays: true}}"
            }
    )
    Optional<PaymentDS> findById(Long id);
}
