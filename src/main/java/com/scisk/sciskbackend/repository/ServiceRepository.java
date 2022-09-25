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

}
