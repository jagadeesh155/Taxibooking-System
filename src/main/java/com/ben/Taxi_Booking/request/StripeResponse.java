package com.ben.Taxi_Booking.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a constructor with no arguments
@AllArgsConstructor // Generates a constructor with all fields as arguments
@Builder // <--- THIS is the CRITICAL fix, generates the static builder() method
public class StripeResponse {

    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;

}