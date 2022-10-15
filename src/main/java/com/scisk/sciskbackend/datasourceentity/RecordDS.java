package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.*;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.RECORD_COLLECTION_NAME)
public class RecordDS {

    @Id
    private Long id;
    private String code;

    private Long customerId;
    private UserDS customer;

    private Long managerId;
    private UserDS manager;

    private Long serviceId;
    private ServiceDS service;

    private Instant createdOn;
    private Boolean suspended;
    private Instant suspensionDate;
    private String suspensionReason;
    private Boolean paid;

    private List<PaymentDS> payments;

    private List<com.scisk.sciskbackend.entity.Document> documents;

    private List<RecordStepDS> recordSteps;

    public Optional<Long> getManagerId() {
        return Optional.ofNullable(managerId);
    }

    public static Record map(RecordDS recordDS) {
        return Record.builder()
                .id(recordDS.getId())
                .code(recordDS.getCode())
                .customer(Objects.isNull(recordDS.getCustomer()) ? null : UserDS.map(recordDS.getCustomer()))
                .manager(Objects.isNull(recordDS.getManager()) ? null : UserDS.map(recordDS.getManager()))
                .service(Objects.isNull(recordDS.getService()) ? null : ServiceDS.map(recordDS.getService()))
                .createdOn(recordDS.getCreatedOn())
                .suspended(recordDS.getSuspended())
                .suspensionDate(recordDS.getSuspensionDate())
                .suspensionReason(recordDS.getSuspensionReason())
                .paid(recordDS.getPaid())
                .payments(Objects.isNull(recordDS.getPayments()) ? null : recordDS.getPayments().stream().map(PaymentDS::map).collect(Collectors.toList()))
                .documents(recordDS.getDocuments())
                .recordSteps(Objects.isNull(recordDS.getRecordSteps()) ? null : recordDS.getRecordSteps().stream().map(RecordStepDS::map).collect(Collectors.toList()))
                .build();
    }

    public static RecordDS map(Record record) {
        return RecordDS.builder()
                .id(record.getId())
                .code(record.getCode())
                .customerId(Objects.isNull(record.getCustomer()) ? null : record.getCustomer().getId())
                .managerId(record.getManager().orElse(User.builder().build()).getId())
                .serviceId(Objects.isNull(record.getService()) ? null : record.getService().getId())
                .createdOn(record.getCreatedOn())
                .suspended(record.getSuspended())
                .suspensionDate(record.getSuspensionDate())
                .suspensionReason(record.getSuspensionReason())
                .paid(record.getPaid())
                .documents(record.getDocuments())
                .build();
    }

}
