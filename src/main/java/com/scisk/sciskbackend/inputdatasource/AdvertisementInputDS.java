package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.dto.AdvertisementListDto;
import com.scisk.sciskbackend.entity.Advertisement;
import com.scisk.sciskbackend.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface AdvertisementInputDS {

    void save(Advertisement advertisement);

    List<Advertisement> findAllEnabled();
}
