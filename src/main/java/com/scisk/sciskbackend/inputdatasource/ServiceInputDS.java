package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceInputDS {

    List<Service> getById(Long id);

    Service save(Service service);

    Optional<Service> findById(Long idValue);
}
