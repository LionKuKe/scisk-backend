package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.Record;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface RecordService {

    RecordReturnDto create(RecordCreateDto recordCreateDto);

    RecordReturnDto update(Long idValue, RecordCreateDto recordCreateDto);

    Page<RecordReturnDto> findAllRecordByFilters(Integer page, Integer size);

    Page<RecordReturnDto> findAllForCustomers(Integer page, Integer size);

    RecordReturnDto findById(Long idValue);

    Record getById(Long idValue);

    RecordReturnDto suspend(Long idValue, String reason);

    void uploadDocument(Long id, MultipartFile file, String name, Long neededDocumentId);

    void deleteDocument(Long id, Long documentId);

    ResponseEntity<Resource> downloadDocument(HttpServletRequest request, Long id, Long documentId);
}
