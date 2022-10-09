package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.NeededDocument;

import java.util.Optional;

public interface NeededDocumentInputDS {

    NeededDocument save(NeededDocument neededDocument);

    Optional<NeededDocument> findById(Long neededDocumentIdValue);
}
