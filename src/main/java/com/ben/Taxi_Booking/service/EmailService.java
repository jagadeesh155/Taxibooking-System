package com.ben.Taxi_Booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void userRegistrationEmail(String toEmail, String username, String password) {

        String subject = "Welcome to Taxi Booking - Your Account has been created";

        String body = createUserMessage (username, password);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    public void driverRegistrationEmail(String toEmail, String username, String password) {

        String subject = "Welcome to Taxi Booking - Your Account has been created";


        String body = createDriverMessage (username, password);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    public void sendOtp(String toEmail, int otp) {

        String subject = "OTP for Taxi Booking";

        String body = "Your Request is succesfully accepted." +
                "Your OTP is:  " + otp
                + "\n\nThank you";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    public String createUserMessage(String username, String password) {
        return "Dear" + username + ",\n\n" +
                "Welcome to Taxi Booking!\n\n" +
                "Your username: " + username + "\n" +
                "Your password: " + password + "\n\n" +
                "Please use the above credentials to log in to your account.\n\n" +
                "If you have any questions or need further assistance, please don't hesitate to contact our customer support team.\n\n" +
                "Thank you for choosing Taxi Booking.";
    }

    public String createDriverMessage(String username, String password) {
        return "Dear" + username + ",\n\n" +
                "Thank you for joining Taxi Booking!\n\n" +
                "Your username: " + username + "\n" +
                "Your password: " + password + "\n\n" +
                "Please use the above credentials to log in to your account.\n\n" +
                "If you have any questions or need further assistance, please don't hesitate to contact our customer support team.\n\n" +
                "Welcome to Our family";
    }

}
