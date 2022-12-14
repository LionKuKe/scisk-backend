package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.AdvertisementListDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdvertisementService {
    void create(MultipartFile file, String title, String description, Integer priority);

    List<AdvertisementListDto> findAllEnabled();

    Page<AdvertisementListDto> findAll(Integer page, Integer size, String title, String description);

    void update(Long id, MultipartFile file, String title, String description, Integer priority, Boolean enabled);

    void delete(Long idValue);
}
