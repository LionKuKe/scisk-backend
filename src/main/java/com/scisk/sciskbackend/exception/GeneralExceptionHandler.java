package com.scisk.sciskbackend.exception;

import com.scisk.sciskbackend.responses.ErrorObjectResponse;
import com.scisk.sciskbackend.responses.OperationResponseModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ivan Kaptue
 */
@Log4j2
@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ERROR_MESSAGE_TEMPLATE = "message: %s %n requested uri: %s";
    public static final String LIST_JOIN_DELIMITER = ",";
    public static final String FIELD_ERROR_SEPARATOR = ": ";
    private static final String ERRORS_FOR_PATH = "errors {} for path {}";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        Map<String, String> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    fieldError -> fieldError.getDefaultMessage() == null ? "" : fieldError.getDefaultMessage()
                )
            );
        return getExceptionResponseEntity(ex, HttpStatus.BAD_REQUEST, request, validationErrors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getLocalizedMessage());
        return getExceptionResponseEntity(ex, status, request, errors);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
        ConstraintViolationException ex, WebRequest request) {
        final Map<String, String> validationErrors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                constraintViolation -> String.valueOf(constraintViolation.getPropertyPath()),
                ConstraintViolation::getMessage
            ));
        return getExceptionResponseEntity(ex, HttpStatus.BAD_REQUEST, request, validationErrors);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getLocalizedMessage());
        return getExceptionResponseEntity(ex, HttpStatus.FORBIDDEN, request, errors);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getLocalizedMessage());
        errors.put("errorDescription", "maxUploadSize");
        return getExceptionResponseEntity(ex, HttpStatus.UNPROCESSABLE_ENTITY, request, errors);
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException ex, WebRequest request) {
        return getExceptionResponseEntity(ex, HttpStatus.NOT_FOUND, request, new HashMap<>());
    }

    @ExceptionHandler({ObjectExistsException.class})
    public ResponseEntity<Object> handleObjectExistsException(ObjectExistsException ex, WebRequest request) {
        return getExceptionResponseEntity(ex, HttpStatus.CONFLICT, request, new HashMap<>());
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        String message = ex.getLocalizedMessage();
        errors.put("message", message);
        return getExceptionResponseEntity(ex, HttpStatus.UNAUTHORIZED, request, errors);
    }

    /**
     * A general handler for all uncaught exs
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ResponseStatus responseStatus =
            ex.getClass().getAnnotation(ResponseStatus.class);
        final HttpStatus status =
            responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        final String localizedMessage = ex.getLocalizedMessage();
        final String path = request.getDescription(false);
        String message = StringUtils.isEmpty(localizedMessage) ? status.getReasonPhrase() : localizedMessage;
        log.error(String.format(ERROR_MESSAGE_TEMPLATE, message, path), ex);

        Map<String, String> errors = new HashMap<>();
        errors.put("message", message);
        return getExceptionResponseEntity(ex, status, request, errors);
    }

    /**
     * Build a detailed information about the ex in the response
     */
    private ResponseEntity<Object> getExceptionResponseEntity(final Exception ex,
                                                              final HttpStatus status,
                                                              final WebRequest request,
                                                              final Map<String, String> errors) {
        ErrorObjectResponse response = new ErrorObjectResponse();
        response.setStatus(status.value());
        response.setMessage(ex.getMessage());
        response.setErrors(errors);

        final String path = request.getDescription(false);

        final String errorsMessage =
            !errors.isEmpty() ?
                errors.entrySet().stream()
                    .map(entrySet -> entrySet.getKey() + FIELD_ERROR_SEPARATOR + entrySet.getValue())
                    .collect(Collectors.joining(LIST_JOIN_DELIMITER))
                : status.getReasonPhrase();
        log.error(ERRORS_FOR_PATH, errorsMessage, path);

        return ResponseEntity.status(status).body(response);
    }
}
