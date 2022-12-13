package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.Advertisement;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.ADVERTISEMENT_COLLECTION_NAME)
public class AdvertisementDS {

    @Id
    private Long id;
    private String title;
    private String description;
    private Binary content;
    private Instant createdAt;
    private Integer priority;
    private Boolean enabled;

    public static AdvertisementDS map(Advertisement advertisement) {
        return AdvertisementDS.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle())
                .description(advertisement.getDescription())
                .content(advertisement.getContent())
                .createdAt(advertisement.getCreatedAt())
                .priority(advertisement.getPriority())
                .enabled(advertisement.getEnabled())
                .build();
    }

    public static Advertisement map(AdvertisementDS advertisement) {
        return Advertisement.builder()
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
