package com.scisk.sciskbackend.entity;

import lombok.*;
import org.bson.types.Binary;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
	private Long id;
	private String name;
	private String extension;
	private Binary content;

	private NeededDocument neededDocument;
	private Long neededDocumentId;
}
