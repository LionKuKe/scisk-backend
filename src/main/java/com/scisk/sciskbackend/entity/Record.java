package com.scisk.sciskbackend.entity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Record {

    private Long id;

    private String code;

    private User customer;

    private User manager;

    private Service service;

    private Instant createdOn;

    private Boolean suspended;

    private Instant suspensionDate;

    private String suspensionReason;

    /**
     * Booléen indiquant si le dossier est payé ou pas
     */
    private Boolean paid;

    private List<Payment> payments;

    private List<com.scisk.sciskbackend.entity.Document> documents;

    private List<RecordStep> recordSteps;

    public Optional<User> getManager() {
        return Optional.ofNullable(manager);
    }

}
