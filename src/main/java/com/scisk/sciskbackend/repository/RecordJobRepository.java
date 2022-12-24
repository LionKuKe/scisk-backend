package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.JobDS;
import com.scisk.sciskbackend.datasourceentity.RecordJobDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordJobRepository extends MongoRepository<RecordJobDS, Long> {
    List<RecordJobDS> findAllByJob(JobDS jobDS);
}
