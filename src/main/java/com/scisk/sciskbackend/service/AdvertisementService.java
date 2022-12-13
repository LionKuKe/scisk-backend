package com.scisk.sciskbackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface AdvertisementService {
    void create(MultipartFile file, String title, String description, Integer priority);
}
