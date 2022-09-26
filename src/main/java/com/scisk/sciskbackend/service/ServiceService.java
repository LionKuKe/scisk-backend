package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import org.springframework.data.domain.Page;

public interface ServiceService {

    ServiceReturnDto create(ServiceCreateDto serviceCreateDto);

    ServiceReturnDto update(Long idValue, ServiceUpdateDto serviceUpdateDto);

    Page<ServiceReturnDto> findAllServiceByFilters(Integer page, Integer size, String name, String description);

    Page<ServiceReturnDto> findAllForCustomers(Integer page, Integer size, String name, String description);

    ServiceReturnDto findById(Long idValue);

    StepReturnDto createStep(Long idValue, StepCreateDto stepCreateDto);

    NeededDocumentReturnDto createNeededDocument(Long idValue, NeededDocumentCreateDto neededDocumentCreateDto);

    StepReturnDto updateStep(Long idValue, Long stepIdValue, StepCreateDto stepCreateDto);

    NeededDocumentReturnDto updateNeededDocument(Long idValue, Long neededDocumentIdValue, NeededDocumentCreateDto neededDocumentCreateDto);
}
