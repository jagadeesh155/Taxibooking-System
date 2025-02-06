package com.ben.Taxi_Booking.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetail> userException(UserException userException, WebRequest webRequest) {
        ErrorDetail errorDetail = new ErrorDetail(userException.getMessage(), webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(DriverException.class)
    public ResponseEntity<ErrorDetail> driverException(DriverException driverException, WebRequest webRequest) {
        ErrorDetail errorDetail = new ErrorDetail(driverException.getMessage(), webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(RideException.class)
    public ResponseEntity<ErrorDetail> rideException(RideException rideException, WebRequest webRequest) {
        ErrorDetail errorDetail = new ErrorDetail(rideException.getMessage(), webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetail> handleValidationException(ConstraintViolationException ex, WebRequest webRequest) {

        StringBuilder message = new StringBuilder();
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            message.append(violation.getMessage());
        }
        ErrorDetail errorDetail = new ErrorDetail(message.toString(), webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> methodArgumentNotValidException(Exception ex, WebRequest webRequest) {
        ErrorDetail errorDetail = new ErrorDetail(ex.getMessage(), webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> otherException(Exception ex, WebRequest webRequest) {
        ErrorDetail errorDetail = new ErrorDetail(ex.getMessage(), webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(errorDetail);
    }


}
