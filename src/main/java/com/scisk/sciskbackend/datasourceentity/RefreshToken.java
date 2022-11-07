package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(GlobalParams.REFRESHTOKEN_COLLECTION_NAME)
public class RefreshToken {
    @Id
    private long id;
    private User user;
    private String token;
    private Instant expiryDate;
}
