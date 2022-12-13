package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.AdvertisementDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends MongoRepository<AdvertisementDS, Long> {
    List<AdvertisementDS> findAllByEnabledOrderByPriorityAsc(Boolean enabled);
}
