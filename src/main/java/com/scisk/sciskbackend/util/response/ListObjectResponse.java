package com.scisk.sciskbackend.util.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ListObjectResponse<T> extends OperationResponse {

    private List<T> items;

    public ListObjectResponse(String operationMessage) {
        super(operationMessage);
    }

    public ListObjectResponse(String operationMessage, List<T> items) {
        super(operationMessage);
        this.items = items;
    }

}
