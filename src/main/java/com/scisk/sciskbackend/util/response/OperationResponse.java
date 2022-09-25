package com.scisk.sciskbackend.util.response;

import lombok.Builder;
import lombok.Data;

/**
 * This is the common structure for all responses if the response contains a
 * list(array) then it will have 'items' field if the response contains a single
 * item then it will have 'item' field
 *
 * @author klionel
 */
@Data
@Builder
public class OperationResponse {

    private String operationMessage;

    public OperationResponse() {
    }

    public OperationResponse(String operationMessage) {
        this.operationMessage = operationMessage;
    }

}
