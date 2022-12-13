package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.AdvertisementDS;
import com.scisk.sciskbackend.entity.Advertisement;
import com.scisk.sciskbackend.repository.AdvertisementRepository;
import com.scisk.sciskbackend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Advertisement> findAllEnabled() {
        return advertisementRepository.findAllByEnabledOrderByPriorityAsc(true)
                .stream()
                .map(AdvertisementDS::map)
                .collect(Collectors.toList());
    }
}
