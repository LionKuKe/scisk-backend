package com.scisk.sciskbackend.util.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SimpleObjectResponse<T> extends OperationResponse {

    private T item;

    public SimpleObjectResponse(String operationMessage) {
        super(operationMessage);
    }

    public SimpleObjectResponse(String operationMessage, T item) {
        super(operationMessage);
        this.item = item;
    }
}
