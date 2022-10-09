package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordReturnDto {
    private Long id;
    private String code;

    private String customerName;
    private Long customerId;

    private String managerName;
    private Long managerId;

    private String serviceName;
    private Long serviceId;

    private Instant createdOn;

    private List<PaymentReturnDto> payments;

    private List<com.scisk.sciskbackend.entity.Document> documents;

    private List<RecordStepReturnDto> recordSteps;

    public static RecordReturnDto map(Record record) {
        return RecordReturnDto.builder()
                .id(record.getId())
                .code(record.getCode())
                .customerId(Objects.isNull(record.getCustomer()) ? null : record.getCustomer().getId())
                .customerName(Objects.isNull(record.getCustomer()) ? null : record.getCustomer().getName())
                .managerId(record.getManager().orElse(User.builder().build()).getId())
                .managerName(record.getManager().isEmpty() ? null : record.getManager().get().getName())
                .serviceId(Objects.isNull(record.getService()) ? null : record.getService().getId())
                .serviceName(record.getService().getName())
                .createdOn(record.getCreatedOn())
                .documents(record.getDocuments())
                .payments(
                        (Objects.isNull(record.getPayments()) || record.getPayments().size() == 0) ?
                                null :
                                record.getPayments().stream().map(PaymentReturnDto::map).collect(Collectors.toList())
                )
                .recordSteps(
                        (Objects.isNull(record.getRecordSteps()) || record.getRecordSteps().size() == 0) ?
                                null :
                                record.getRecordSteps().stream().map(RecordStepReturnDto::map).collect(Collectors.toList())
                )
                .build();
    }
}
