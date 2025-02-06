# **City Taxi Application**

A Spring Boot-based taxi booking system where passengers can book, start, complete, and cancel trips. The system allows drivers to accept trips, and passengers are notified via email when the trip status changes. Stripe is Used for Accepting Payments.
 
## Table of Contents

- Project Overview
- Technologies Used
- Prerequisites
- Installation
- Configuration
  - Database Configuration
- How to Run the Project
- Key Features
  - Booking a Trip
  - Starting a Trip
  - Completing a Trip
  - Canceling a Trip
  - Driver Acceptance
  - Security Configuration
- API Endpoints
- Contact

## Project Overview

This project simulates a taxi booking system where passengers can:
- Book a trip.
- Start the trip when the driver is selected.
- Complete the trip and calculate the fare.
- Cancel the trip before the driver has started it.

The application uses Emails to send SMS notifications to both passengers and drivers during the key events of the trip.

The system also uses Stripe to accept payments for trips.


## Technologies Used

- Java 22
- Spring Boot 3.3.3
- MySQL
- Spring Data JPA
- Email Service
- Hibernate for ORM
- MapStruct for DTO mapping
- Stripe for accepting payments
- Spring Security for authentication

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java 17 or later installed on your machine.
- Maven installed to manage dependencies.
- MySQL installed for the database.
- Email account for notification.
- Stripe account for payments.

## Installation

1. Clone the repository:
   
   git clone https://github.com/AdityaZaware/Taxi-Booking

2. Navigate to the project directory:

   cd taxi-booking

3. Install dependencies:

   mvn clean install

## Configuration

### MySQL Configuration

Before running the application, you need to configure the MySQL database. You can do this by modifying the `application.properties` file in the `src/main/resources` directory.

### Email Configuration

Set your email name and password in the `application.properties` file in the `src/main/resources` directory.

### Stripe Configuration

Set your Stripe API key in the `application.properties` file in the `src/main/resources` directory.


## API Endpoints

| Endpoint                      | Method | Description                      |
|-------------------------------|--------|----------------------------------|
| `/api/auth/user/signup`       | POST   | Register a new user              |
| `/api/auth/signup`            | POST   | Login to the system              |
| `/api/auth//driver/signup`    | POST   | Register a new Driver            |
| `/user`                       | GET    | List all players                 |
| `/user/{id}`                  | GET    | Get details of a specific player |
| `/user/profile`               | GET    | Get User Profile                 |
| `/user/rides/completed`       | GET    | Get User completed Rides         |
| `/user/payment/{id}`          | POST   | Pay Through Stripe               |
| `/driver/profile`             | GET    | Get Driver Profile               |
| `/driver/{id}/currentRide`    | GET    | Get Driver Current Drive         |
| `/driver/{id}/allocatedRides` | GET    | Get allocated Ride               |
| `/driver/rides/completed`     | GET    | Get completed Rides              |
| `/ride/request`               | POST   | Create a new Ride                |
| `/ride/accept/{rideId}`       | PUT    | Accept Ride                      |
| `/ride/reject/{rideId}`       | PUT    | Reject Ride                      |
| `/ride/start/{rideId}`        | PUT    | Start ride                       |
| `/ride/complete/{rideId}`     | PUT    | Finish the ride                  |
| `/ride/rideId`                | GET    | Find ride by id                  |


## Contact

If you are having any issue ask me on : https://www.linkedin.com/in/aditya-zaware-310904259/