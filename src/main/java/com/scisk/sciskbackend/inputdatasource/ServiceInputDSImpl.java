package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.ServiceDS;
import com.scisk.sciskbackend.entity.Service;
import com.scisk.sciskbackend.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceInputDSImpl implements ServiceInputDS {

    private final ServiceRepository serviceRepository;

    public ServiceInputDSImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<Service> getById(Long id) {
        return serviceRepository.getById(id)
                .stream()
                .map(ServiceDS::map)
                .collect(Collectors.toList());
    }

    @Override
    public Service save(Service service) {
        serviceRepository.save(ServiceDS.map(service));
        return service;
    }

    @Override
    public Optional<Service> findById(Long idValue) {
        return serviceRepository.findById(idValue).map(ServiceDS::map);
    }
}
