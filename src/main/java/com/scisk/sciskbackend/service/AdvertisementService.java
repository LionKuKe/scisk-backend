package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.AdvertisementListDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdvertisementService {
    void create(MultipartFile file, String title, String description, Integer priority);

    List<AdvertisementListDto> findAllEnabled();
}
