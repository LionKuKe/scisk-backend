package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.AdvertisementDS;
import com.scisk.sciskbackend.entity.Advertisement;
import com.scisk.sciskbackend.repository.AdvertisementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    @Override
    public Page<Advertisement> findAllPaginatedByCriterias(
            Integer page,
            Integer size,
            String title,
            String description
    ) {
        Page<AdvertisementDS> advertisementDS = advertisementRepository.findAllByTitleContainsIgnoreCaseAndDescriptionContainsIgnoreCase(
                title,
                description,
                PageRequest.of(page, size)
        );
        return new PageImpl<>(
                advertisementDS.getContent().stream().map(AdvertisementDS::map).collect(Collectors.toList()),
                advertisementDS.getPageable(),
                advertisementDS.getTotalElements()
        );
    }

    @Override
    public Optional<Advertisement> findById(Long idValue) {
        return advertisementRepository.findById(idValue).map(AdvertisementDS::map);
    }

    @Override
    public void delete(Advertisement advertisement) {
        Optional<AdvertisementDS> optionalAdvertisementDS = advertisementRepository.findById(advertisement.getId());
        optionalAdvertisementDS.ifPresent(advertisementRepository::delete);
    }

}
