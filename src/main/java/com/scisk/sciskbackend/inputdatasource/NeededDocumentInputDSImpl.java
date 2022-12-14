package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.NeededDocumentDS;
import com.scisk.sciskbackend.entity.NeededDocument;
import com.scisk.sciskbackend.repository.NeededDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class NeededDocumentInputDSImpl implements NeededDocumentInputDS {

    private final NeededDocumentRepository neededDocumentRepository;

    public NeededDocumentInputDSImpl(NeededDocumentRepository neededDocumentRepository) {
        this.neededDocumentRepository = neededDocumentRepository;
    }

    @Override
    public NeededDocument save(NeededDocument neededDocument) {
        neededDocumentRepository.save(NeededDocumentDS.map(neededDocument));
        return neededDocument;
    }

    @Override
    public Optional<NeededDocument> findById(Long neededDocumentIdValue) {
        return neededDocumentRepository.findById(neededDocumentIdValue).map(NeededDocumentDS::map);
    }
}
