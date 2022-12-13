package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.entity.Advertisement;
import com.scisk.sciskbackend.inputdatasource.*;
import com.scisk.sciskbackend.util.GlobalParams;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Transactional
@Service
public class AdvertisementServImpl implements AdvertisementService {

    private CounterService counterService;
    private final MongoTemplate mongoTemplate;
    private final AdvertisementInputDS advertisementInputDS;

    public AdvertisementServImpl(
            CounterService counterService,
            MongoTemplate mongoTemplate,
            AdvertisementInputDS advertisementInputDS
    ) {
        this.counterService = counterService;
        this.mongoTemplate = mongoTemplate;
        this.advertisementInputDS = advertisementInputDS;
    }

    @Override
    public void create(MultipartFile file, String title, String description, Integer priority) {
        try {
            Advertisement advertisement = Advertisement.builder()
                    .id(counterService.getNextSequence(GlobalParams.ADVERTISEMENT_COLLECTION_NAME))
                    .title(title)
                    .description(description)
                    .content(new Binary(BsonBinarySubType.BINARY, file.getBytes()))
                    .createdAt(Instant.now())
                    .priority(priority)
                    .enabled(true)
                    .build();
            advertisementInputDS.save(advertisement);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
