package com.ben.Taxi_Booking.exception;

public class ErrorDetail {

    private String error;
    private String message;

    public ErrorDetail(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
