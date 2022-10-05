package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, Long> {


    @Query("{recordId:'?0'}")
    List<Payment> findByRecordId(Long recordId);
}
