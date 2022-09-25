package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.ServiceCreateDto;
import com.scisk.sciskbackend.dto.ServiceReturnDto;
import org.springframework.data.domain.Page;

public interface ServiceService {

    ServiceReturnDto create(ServiceCreateDto serviceCreateDto);

    ServiceReturnDto update(Long idValue, ServiceCreateDto serviceCreateDto);

    Page<ServiceReturnDto> findAllServiceByFilters(Integer page, Integer size, String name, String description);

    Page<ServiceReturnDto> findAllForCustomers(Integer page, Integer size, String name, String description);

    ServiceReturnDto findById(Long idValue);
}
