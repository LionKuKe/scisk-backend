package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.NeededDocument;
import com.scisk.sciskbackend.entity.Service;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.NEEDED_DOCUMENT_COLLECTION_NAME)
public class NeededDocumentDS {

    @Id
    private Long id;
    private String name;
    private Long serviceId;
    private ServiceDS service;
    private Boolean enabled;

    public static NeededDocument map(NeededDocumentDS neededDocumentDS) {
        return NeededDocument.builder()
                .id(neededDocumentDS.getId())
                .name(neededDocumentDS.getName())
                .service(Service.builder().id(neededDocumentDS.getServiceId()).build())
                .enabled(neededDocumentDS.getEnabled())
                .build();
    }

    public static NeededDocumentDS map(NeededDocument neededDocument) {
        return NeededDocumentDS.builder()
                .id(neededDocument.getId())
                .name(neededDocument.getName())
                .serviceId(Objects.isNull(neededDocument.getService()) ? null : neededDocument.getService().getId())
                .enabled(neededDocument.getEnabled())
                .build();
    }

}
