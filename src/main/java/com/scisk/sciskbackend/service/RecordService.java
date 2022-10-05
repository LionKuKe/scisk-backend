package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.Record;
import org.springframework.data.domain.Page;

public interface RecordService {

    RecordReturnDto create(RecordCreateDto recordCreateDto);

    RecordReturnDto update(Long idValue, RecordCreateDto recordCreateDto);

    Page<RecordReturnDto> findAllRecordByFilters(Integer page, Integer size);

    Page<RecordReturnDto> findAllForCustomers(Integer page, Integer size);

    RecordReturnDto findById(Long idValue);

    Record getById(Long idValue);

    RecordReturnDto suspend(Long idValue, String reason);
}
