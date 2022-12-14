package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.Advertisement;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AdvertisementInputDS {

    void save(Advertisement advertisement);

    List<Advertisement> findAllEnabled();

    Page<Advertisement> findAllPaginatedByCriterias(Integer page, Integer size, String title, String description);

    Optional<Advertisement> findById(Long idValue);

    void delete(Advertisement advertisement);
}
