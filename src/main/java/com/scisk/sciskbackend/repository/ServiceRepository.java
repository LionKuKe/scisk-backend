package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.entity.Service;
import com.scisk.sciskbackend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends MongoRepository<Service, Long> {

    @Query(
            value = "[" +
                    "{_id:'?0'}, " +
                    "{$lookup : {from : 'step', localField : '_id', foreignField : 'serviceId', as : 'steps'}}, " +
                    "{$lookup : {from : 'neededdocument', localField : '_id', foreignField : 'serviceId', as : 'neededDocuments'}} " +
                    "]"
    )
    List<Service> getById(Long id);
}
