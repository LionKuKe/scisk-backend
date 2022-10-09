package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.ServiceDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends MongoRepository<ServiceDS, Long> {

    @Query(
            value = "[" +
                    "{_id:'?0'}, " +
                    "{$lookup : {from : 'step', localField : '_id', foreignField : 'serviceId', as : 'steps'}}, " +
                    "{$lookup : {from : 'neededdocument', localField : '_id', foreignField : 'serviceId', as : 'neededDocuments'}} " +
                    "]"
    )
    List<ServiceDS> getById(Long id);
}
