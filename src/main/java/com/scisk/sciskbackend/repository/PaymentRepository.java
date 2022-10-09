package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.PaymentDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentDS, Long> {


    @Query("{recordId:'?0'}")
    List<PaymentDS> findByRecordId(Long recordId);
}
