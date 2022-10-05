package com.scisk.sciskbackend.entity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.scisk.sciskbackend.util.GlobalParams;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.RECORD_COLLECTION_NAME)
public class Record {

    @Id
    private Long id;
    private String code;

    private User customer;
    private Long customerId;

    private User manager;
    private Long managerId;

    private Service service;
    private Long serviceId;

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

    public void setCustomer(User customer) {
        this.customer = customer;
        this.customerId = Objects.isNull(customer) ? null : customer.getId();
    }

    public Optional<User> getManager() {
        return Optional.ofNullable(manager);
    }

    public void setManager(User manager) {
        this.manager = manager;
        this.managerId = Objects.isNull(manager) ? null : manager.getId();
    }

    public void setService(Service service) {
        this.service = service;
        this.serviceId = Objects.isNull(service) ? null : service.getId();
    }


}
