package com.ben.Taxi_Booking.controller;

import com.ben.Taxi_Booking.config.JwtProviderUtil;
import com.ben.Taxi_Booking.entity.Driver;
import com.ben.Taxi_Booking.entity.User;
import com.ben.Taxi_Booking.enums.UserRole;
import com.ben.Taxi_Booking.exception.UserException;
import com.ben.Taxi_Booking.repo.DriverRepo;
import com.ben.Taxi_Booking.repo.UserRepo;
import com.ben.Taxi_Booking.request.DriverSignUpRequest;
import com.ben.Taxi_Booking.request.LoginRequest;
import com.ben.Taxi_Booking.request.SignupRequest;
import com.ben.Taxi_Booking.service.CustomUserDetailsService;
import com.ben.Taxi_Booking.service.DriverService;
import com.ben.Taxi_Booking.service.EmailService;
import com.ben.Taxi_Booking.util.JwtResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.prefs.BackingStoreException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepo userRepo;
    private final DriverRepo driverRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProviderUtil jwtProviderUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final DriverService driverService;
    private final EmailService emailService;

    public AuthController(UserRepo userRepo, DriverRepo driverRepo, PasswordEncoder passwordEncoder, JwtProviderUtil jwtProviderUtil, CustomUserDetailsService customUserDetailsService, DriverService driverService, EmailService emailService) {
        this.userRepo = userRepo;
        this.driverRepo = driverRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtProviderUtil = jwtProviderUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.driverService = driverService;
        this.emailService = emailService;
    }

    // --- USER SIGNUP ---
    @PostMapping("/user/signup")
    public ResponseEntity<JwtResponse> signup(@RequestBody SignupRequest signupRequest) throws UserException {

        String email = signupRequest.getEmail();
        String name = signupRequest.getName();
        String password = signupRequest.getPassword();
        String mobile = signupRequest.getMobile();

        User user = userRepo.findByEmail(email);

        if(user != null) {
            throw new UserException("User already exists: " + email);
        }

        String encodePassword = passwordEncoder.encode(password);

        User save = new User();
        save.setEmail(email);
        save.setPassword(encodePassword);
        save.setName(name);
        save.setMobile(mobile);
        save.setRole(UserRole.USER);

        User saveUser = userRepo.save(save);

        emailService.userRegistrationEmail(saveUser.getEmail(), saveUser.getName(), password);

        // Load UserDetails to get authorities
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(saveUser.getEmail());

        // Create authenticated token
        Authentication authenticatedToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities() // Credentials set to null
        );

        String jwt = jwtProviderUtil.generateJwtToken(authenticatedToken);

        JwtResponse response = new JwtResponse();
        response.setJwt(jwt);
        response.setAuthenticated(true);
        response.setError(false);
        response.setErrorMessage(null);
        response.setRole(UserRole.USER);
        response.setMessage("User created successfully: " + saveUser.getName());

        return new ResponseEntity<JwtResponse>(response,HttpStatus.OK);
    }

    // --- DRIVER SIGNUP ---
    @PostMapping("/driver/signup")
    public ResponseEntity<JwtResponse> driverSignUp(@RequestBody DriverSignUpRequest driverSignUpRequest) {

        Driver driver = driverRepo.findByEmail(driverSignUpRequest.getEmail());
        JwtResponse jwtResponse = new JwtResponse();

        if(driver != null) {
            jwtResponse.setAuthenticated(false);
            jwtResponse.setError(true);
            jwtResponse.setErrorMessage("Driver already exists");
            jwtResponse.setRole(UserRole.DRIVER);
            jwtResponse.setMessage("Driver already exists");
            return new ResponseEntity<>(jwtResponse,HttpStatus.BAD_REQUEST);
        }

        Driver createdDriver = driverService.registerDriver(driverSignUpRequest);

        // Re-load UserDetails to get authorities
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(createdDriver.getEmail());

        // Create authenticated token
        Authentication authenticatedToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities() // Credentials set to null
        );

        String jwt = jwtProviderUtil.generateJwtToken(authenticatedToken);

        JwtResponse response = new JwtResponse();
        response.setJwt(jwt);
        response.setAuthenticated(true);
        response.setError(false);
        response.setErrorMessage(null);
        response.setRole(UserRole.DRIVER);
        response.setMessage("Account created successfully: " + createdDriver.getName());

        return new ResponseEntity<JwtResponse>(response,HttpStatus.OK);
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) throws BackingStoreException {

        String email = request.getEmail();
        String password = request.getPassword();

        // Call the helper method to authenticate
        Authentication auth = AuthenticateAction(email, password);

        // Generate JWT using the authenticated object
        String jwt = jwtProviderUtil.generateJwtToken(auth);

        JwtResponse response = new JwtResponse();
        response.setJwt(jwt);
        response.setAuthenticated(true);
        response.setError(false);
        response.setErrorMessage(null);

        // Extract role from the successfully authenticated object
        response.setRole(UserRole.valueOf(auth.getAuthorities().iterator().next().getAuthority().substring(5)));
        response.setMessage("Your account logged in successfully");

        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    // --- AUTHENTICATION HELPER METHOD ---
    private Authentication AuthenticateAction(String email, String password) throws BadCredentialsException {
        // Load the user details (contains email, HASHED password, and authorities)
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid User/Email");
        }

        // 1. Match the raw password (from Postman) against the HASHED password (from DB)
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        // 2. Return an AUTHENTICATED token with the user's authorities (roles).
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),        // Principal (e.g., email)
                null,                             // Credentials (set to null for authenticated token)
                userDetails.getAuthorities()      // Authorities (Roles)
        );
    }
}