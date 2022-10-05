package com.scisk.sciskbackend.entity;

import lombok.*;
import org.springframework.data.annotation.Transient;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
	private String name;
	private String path;
	private String extension;
	
	private Record record;

	@Transient
	private NeededDocument neededDocument;
	private Long neededDocumentId;
}
