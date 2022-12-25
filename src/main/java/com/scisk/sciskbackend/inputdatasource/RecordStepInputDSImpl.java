package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.RecordStepDS;
import com.scisk.sciskbackend.entity.RecordStep;
import com.scisk.sciskbackend.repository.RecordStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class RecordStepInputDSImpl implements RecordStepInputDS {

    private final RecordStepRepository recordStepRepository;

    public RecordStepInputDSImpl(RecordStepRepository recordStepRepository) {
        this.recordStepRepository = recordStepRepository;
    }

    @Override
    public List<RecordStep> findAllByRecordId(Long recordId) {
        return recordStepRepository.findAllByRecordId(recordId).stream()
                .map(RecordStepDS::map)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<RecordStep> recordSteps) {
        recordStepRepository.saveAll(recordSteps.stream().map(RecordStepDS::map).collect(Collectors.toList()));
    }

    @Override
    public Optional<RecordStep> findById(Long idValue) {
        return recordStepRepository.findById(idValue).map(RecordStepDS::map);
    }

    @Override
    public Optional<RecordStep> findByRecordJobId(Long id) {
        return recordStepRepository.findByRecordJobId(id).map(RecordStepDS::map);
    }

}
