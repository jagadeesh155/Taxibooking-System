package com.ben.Taxi_Booking.controller;


import com.ben.Taxi_Booking.exception.RideException;
import com.ben.Taxi_Booking.request.StripeResponse;
import com.ben.Taxi_Booking.service.StripService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class PaymentController{

    private final StripService stripService;


    public PaymentController(StripService stripService) {
        this.stripService = stripService;
    }

    @PostMapping("/payment/{id}")
    public ResponseEntity<StripeResponse> onlinePayment(@PathVariable Integer id, @RequestHeader("Authorization") String jwt) throws StripeException, RideException {

        StripeResponse stripeResponse = stripService.payment(id);

        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }
}
