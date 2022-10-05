package com.scisk.sciskbackend.entity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.scisk.sciskbackend.util.GlobalParams;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.PAYMENT_COLLECTION_NAME)
public class Payment {

    @Id
    private Long id;
    private Instant paymentDate;
    private Double amount;
    private String observation;

    private Record record;
    private List<Record> records;
    private Long recordId;

    public void setRecord(Record record) {
        this.record = record;
        this.recordId = Objects.isNull(record) ? null : record.getId();
    }

    public Record getRecord() {
        return (!Objects.isNull(records) && records.size() > 0) ? records.get(0) : record;
    }
}
