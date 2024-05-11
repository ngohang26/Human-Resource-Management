package com.hrm.Human.Resource.Management.response;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

// GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errorMessage.append(error.getDefaultMessage()).append("; ");
        });
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getConstraintViolations().forEach((violation) -> {
            errorMessage.append(violation.getMessage()).append("; ");
        });
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<String> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        if (rootCause instanceof ConstraintViolationException) {
            // Xử lý giống như ConstraintViolationException
            ConstraintViolationException consEx = (ConstraintViolationException) rootCause;
            StringBuilder errorMessage = new StringBuilder();
            consEx.getConstraintViolations().forEach((violation) -> {
                errorMessage.append(violation.getMessage()).append(";");
            });
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rootCause.getMessage());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
//        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
//            org.hibernate.exception.ConstraintViolationException consEx = (org.hibernate.exception.ConstraintViolationException) ex.getCause();
//            if (consEx.getConstraintName().equals("employees.UK_g6512s2t9cous2oxa17he4irp")) {
//                return new ResponseEntity<>("Số điện thoại đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST);
//            }
//        }
        // Lấy root cause để có thông tin chi tiết hơn về lỗi
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(rootCause.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
