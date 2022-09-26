package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import org.springframework.data.domain.Page;

public interface RecordService {

    RecordReturnDto create(RecordCreateDto recordCreateDto);

    RecordReturnDto update(Long idValue, RecordCreateDto recordCreateDto);

    Page<RecordReturnDto> findAllRecordByFilters(Integer page, Integer size, String name, String description);

    Page<RecordReturnDto> findAllForCustomers(Integer page, Integer size, String name, String description);

    RecordReturnDto findById(Long idValue);

    RecordReturnDto suspend(Long idValue);
}
