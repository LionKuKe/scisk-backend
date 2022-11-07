package com.scisk.sciskbackend.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ErrorObjectResponse extends OperationResponseModel {

    public ErrorObjectResponse(Map<String, String> errors) {
        // super(operationStatus);
        this.errors = errors;
    }

    private int status;
    private Map<String, String> errors = new HashMap<>();
}
