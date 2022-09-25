package com.scisk.sciskbackend.entity;

import java.util.Objects;

import com.scisk.sciskbackend.util.GlobalParams;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.NEEDED_DOCUMENT_COLLECTION_NAME)
public class NeededDocument {
	
	@Id
	private Long id;
	private String name;
	
	@Transient
	private Service service;
	private Long serviceId;

	private Boolean enabled;
	
	public void setService(Service service) {
		this.service = service;
		this.serviceId = Objects.isNull(service) ? null : service.getId();
	}

}
