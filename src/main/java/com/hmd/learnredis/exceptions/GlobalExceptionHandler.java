package com.hmd.learnredis.exceptions;

import com.hmd.learnredis.dtos.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String message, Object errors) {
        return ResponseEntity.status(status).body(ErrorResponseDTO.builder().message(message).errorDetails(errors).build());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameAlreadyExistsException(AlreadyExistsException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Data validation failed", errors);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDTO> handleBindException(BindException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Data binding failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Data violated constraints", errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Data type mismatch", null);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class, TokenExpiredException.class, InvalidJwtException.class})
    public ResponseEntity<ErrorResponseDTO> handleLoginExceptions(RuntimeException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), null);
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<ErrorResponseDTO> handlePasswordMismatchException(PasswordException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }
}
