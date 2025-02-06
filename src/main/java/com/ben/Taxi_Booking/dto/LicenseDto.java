package com.ben.Taxi_Booking.dto;

public class LicenseDto {

    private String number;
    private String expiryDate;

    public LicenseDto() {
    }

    public LicenseDto(String number, String expiryDate) {
        this.number = number;
        this.expiryDate = expiryDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
