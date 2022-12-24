package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import org.springframework.data.domain.Page;

public interface JobService {

    JobReturnDto create(JobCreateDto jobCreateDto);

    JobReturnDto update(Long idValue, JobUpdateDto jobUpdateDto);

    Page<JobReturnDto> findAllByFilters(Integer page, Integer size, String name, String description, Long stepId);

    JobReturnDto findById(Long idValue);

    void delete(Long idValue);
}
