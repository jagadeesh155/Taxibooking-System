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



    @PostMapping("/user/signup")
    public ResponseEntity<JwtResponse> signup(@RequestBody SignupRequest signupRequest) throws UserException {

        String email = signupRequest.getEmail();
        String name = signupRequest.getName();
        String password = signupRequest.getPassword();
        String mobile = signupRequest.getMobile();

        User user = userRepo.findByEmail(email);

        if(user != null) {
            throw new UserException("User already exists" + email);
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

        Authentication authentication = new UsernamePasswordAuthenticationToken(saveUser.getEmail(),
                saveUser.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProviderUtil.generateJwtToken(authentication);

        JwtResponse response = new JwtResponse();

        response.setJwt(jwt);
        response.setAuthenticated(true);
        response.setError(false);
        response.setErrorMessage(null);
        response.setRole(UserRole.USER);
        response.setMessage("User created successfully"+ saveUser);

        return new ResponseEntity<JwtResponse>(response,HttpStatus.OK);
    }

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

        Authentication authentication = new UsernamePasswordAuthenticationToken(createdDriver.getEmail(),
                createdDriver.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProviderUtil.generateJwtToken(authentication);

        JwtResponse response = new JwtResponse();

        response.setJwt(jwt);
        response.setAuthenticated(true);
        response.setError(false);
        response.setErrorMessage(null);
        response.setRole(UserRole.DRIVER);
        response.setMessage("Account created successfully"+ createdDriver.getName());

        return new ResponseEntity<JwtResponse>(response,HttpStatus.OK);


    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) throws BackingStoreException {

        String email = request.getEmail();
        String password = request.getPassword();

        Authentication auth = AuthenticateAction(email, password);


        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtProviderUtil.generateJwtToken(auth);

        JwtResponse response = new JwtResponse();

        response.setJwt(jwt);
        response.setAuthenticated(true);
        response.setError(false);
        response.setErrorMessage(null);
        response.setRole(UserRole.USER);
        response.setMessage("Your account logged in successfully");

        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);


    }

    private Authentication AuthenticateAction(String email, String password) throws BackingStoreException {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if(userDetails == null) {
            throw new BackingStoreException("Invalid User");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails,userDetails);
    }
}
