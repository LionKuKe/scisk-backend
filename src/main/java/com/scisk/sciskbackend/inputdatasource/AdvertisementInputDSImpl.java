package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.AdvertisementDS;
import com.scisk.sciskbackend.entity.Advertisement;
import com.scisk.sciskbackend.repository.AdvertisementRepository;
import com.scisk.sciskbackend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AdvertisementInputDSImpl implements AdvertisementInputDS {

    private final AdvertisementRepository advertisementRepository;

    public AdvertisementInputDSImpl(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    @Override
    public void save(Advertisement advertisement) {
        advertisementRepository.save(AdvertisementDS.map(advertisement));
    }
}
