package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.NeededDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeededDocumentReturnDto {
    private Long id;
    private String name;
    private Boolean enabled;

    public static NeededDocumentReturnDto map(NeededDocument neededDocument) {
        return NeededDocumentReturnDto.builder()
                .id(neededDocument.getId())
                .name(neededDocument.getName())
                .enabled(neededDocument.getEnabled())
                .build();
    }
}
