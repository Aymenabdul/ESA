package com.survey.esa.LoginCredintials;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.survey.esa.jwttoken.JwtUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api2")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * signup controller
     * use the error status i used in the response to show in the front end
     **/
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserTable user) {
        Optional<UserTable> existingUserByEmail = userService.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This email is already registered.");
        }
        Optional<UserTable> existingUserByPhone = userService.findByPhoneNumber(user.getPhoneNumber());
        if (existingUserByPhone.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This phone number is already registered.");
        }
        String hashedPassword = userService.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userService.saveUser(user);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }
    /**
     * get all user detal for use table controller
     * use the error status i used in the response to show in the front end
     **/
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")  // Ensure only ADMINs can access
    public ResponseEntity<List<UserTable>> getAllUsers() {
        List<UserTable> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    /**
     * Login controller
     * you can use both email and password for login
     * use the error status i used in the response to show in the front end
     **/

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserTable loginCredentials) {
        Optional<UserTable> user = userService.findByEmail(loginCredentials.getEmail());
        if (!user.isPresent()) {
            user = userService.findByPhoneNumber(loginCredentials.getPhoneNumber());
        }
        if (user.isPresent()) {
            UserTable foundUser = user.get();
            if ("surveyor".equals(foundUser.getRole()) && !foundUser.isAccept()) {
                return new ResponseEntity<>(
                        Map.of("message", "Your access is denied. Please contact the admin to get access."),
                        HttpStatus.FORBIDDEN);
            }
            boolean passwordMatches = userService.checkPassword(loginCredentials.getPassword(),
                    foundUser.getPassword());
            if (passwordMatches) {
                String token = jwtUtil.generateToken(foundUser.getName(), foundUser.getEmail());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login Successful");
                response.put("token", token);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "Invalid Credentials"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(Map.of("message", "Invalid Credentials"), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Getting user details controller
     * use the token i set her and store the response in the cache while login and pass through all the screen 
     * and make sure the cache is not destroyed when the app is refreshed 
     * only needs to destroyed when the user logged out or session is expiered
     * use the error status i used in the response to show in the front end
     **/
    @GetMapping("/user-details")
    public ResponseEntity<Map<String, Object>> getUserDetails(@AuthenticationPrincipal String email) {
        System.out.println("Extracted email from Security Context: " + email);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication Principal: " + authentication.getPrincipal());

        if (email != null) {
            Optional<UserTable> user = userService.findByEmail(email);
            if (user.isPresent()) {
                UserTable foundUser = user.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", foundUser.getId());
                response.put("name", foundUser.getName());
                response.put("email", foundUser.getEmail());
                response.put("phoneNumber", foundUser.getPhoneNumber());
                response.put("constituency",foundUser.getConstituency());
                response.put("role",foundUser.getRole());

                System.out.println("Returning user details for email: " + email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                System.out.println("User not found for email: " + email);
                return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("Authentication principal is null.");
            return new ResponseEntity<>(Map.of("message", "Authentication principal not found"),
                    HttpStatus.UNAUTHORIZED);
        }
    }
    /**
     * Admin access controller for accepting user login 
     * pass the email as the params and pass the autherization in the header like this 
     * key : autherization , value : Bearer <token>
     **/
    @PutMapping("/activate-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String email) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }
        Optional<UserTable> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserTable user = userOptional.get();
            if (user.isAccept()) {
                return ResponseEntity.ok("User with email " + email + " is already activated.");
            }
            user.setAccept(true);
            userRepository.save(user);
            emailService.sendActivationEmail(user.getEmail(), user.getName());
            return ResponseEntity.ok("User with email " + email + " has been activated and notified.");
        } else {
            return ResponseEntity.badRequest().body("User with email " + email + " not found.");
        }
    }

    /**
     * Admin access controller for decline user login 
     * pass the email as the params and pass the autherization in the header like this 
     * key : autherization , value : Bearer <token>
     **/
    @PutMapping("/decline-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> declineUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String email) {
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }
        Optional<UserTable> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            UserTable user = userOptional.get();
            if (!user.isAccept()) {
                return ResponseEntity.ok("User with email " + email + " is already declined.");
            }
            user.setAccept(false);
            userRepository.save(user);
            emailService.sendDeclineEmail(user.getEmail(), user.getName());

            return ResponseEntity.ok("User with email " + email + " has been declined and notified.");
        } else {
            return ResponseEntity.badRequest().body("User with email " + email + " not found.");
        }
    }

}
