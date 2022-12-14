package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.AdvertisementListDto;
import com.scisk.sciskbackend.entity.Advertisement;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.inputdatasource.*;
import com.scisk.sciskbackend.util.GlobalParams;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<AdvertisementListDto> findAllEnabled() {
        return advertisementInputDS.findAllEnabled().stream().map(AdvertisementListDto::map).collect(Collectors.toList());
    }

    @Override
    public Page<AdvertisementListDto> findAll(Integer page, Integer size, String title, String description) {
        Page<Advertisement> advertisements = advertisementInputDS.findAllPaginatedByCriterias(page, size, title, description);
        return new PageImpl<>(
                advertisements.getContent().stream().map(AdvertisementListDto::map).collect(Collectors.toList()),
                advertisements.getPageable(),
                advertisements.getTotalElements()
        );
    }

    @Override
    public void update(
            Long id,
            MultipartFile file,
            String title,
            String description,
            Integer priority,
            Boolean enabled
    ) {
        Optional<Advertisement> optionalAdvertisement = advertisementInputDS.findById(id);
        if (optionalAdvertisement.isEmpty()) {
            throw new ObjectNotFoundException("id");
        }
        optionalAdvertisement.get().setTitle(title);
        optionalAdvertisement.get().setDescription(description);
        try {
            optionalAdvertisement.get().setContent(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        optionalAdvertisement.get().setPriority(priority);
        optionalAdvertisement.get().setEnabled(enabled);
        advertisementInputDS.save(optionalAdvertisement.get());
    }

    @Override
    public void delete(Long idValue) {
        Optional<Advertisement> optionalAdvertisement = advertisementInputDS.findById(idValue);
        if (optionalAdvertisement.isEmpty()) {
            throw new ObjectNotFoundException("id");
        }
        advertisementInputDS.delete(optionalAdvertisement.get());
    }
}
