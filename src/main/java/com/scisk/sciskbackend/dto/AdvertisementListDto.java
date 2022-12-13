package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.Advertisement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementListDto {
    private Long id;
    private String title;
    private String description;
    private Binary content;
    private Instant createdAt;
    private Integer priority;
    private Boolean enabled;

    public static AdvertisementListDto map(Advertisement advertisement) {
        return AdvertisementListDto.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle())
                .description(advertisement.getDescription())
                .content(advertisement.getContent())
                .createdAt(advertisement.getCreatedAt())
                .priority(advertisement.getPriority())
                .enabled(advertisement.getEnabled())
                .build();
    }
}
