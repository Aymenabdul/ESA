package com.survey.esa.LoginCredintials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.esa.jwttoken.JwtUtil;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * signup controller use the error status i used in the response to show in
     * the front end
     *
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserTable user) {
        // Check if the email is already taken
        Optional<UserTable> existingUserByEmail = userService.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This email is already registered.");
        }

        // Check if the phone number is already taken
        Optional<UserTable> existingUserByPhone = userService.findByPhoneNumber(user.getPhoneNumber());
        if (existingUserByPhone.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This phone number is already registered.");
        }

        // Hash the password before saving it (handled in saveUser)
        userService.saveUser(user);  // This already hashes the password internally

        // Return success response
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    /**
     * get all user detal for use table controller use the error status i used
     * in the response to show in the front end
     *
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")  // Ensure only ADMINs can access this endpoint
    public ResponseEntity<List<UserTable>> getAllSurveyors() {
        // Fetch all users from the service
        List<UserTable> users = userService.getAllUsers();

        // Filter the list to include only users with the "surveyor" role
        List<UserTable> surveyors = users.stream()
                .filter(user -> "Surveyor".equals(user.getRole())) // Filter by role
                .collect(Collectors.toList());

        // Check if the filtered list is empty
        if (surveyors.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no surveyors are found
        }

        // Return the filtered list of surveyors
        return ResponseEntity.ok(surveyors); // Return the surveyors list with HTTP 200 OK status
    }

    /**
     * Login controller you can use both email and password for login use the
     * error status i used in the response to show in the front end
     *
     */
    @PostMapping("/login")
public ResponseEntity<Map<String, String>> login(@RequestBody UserTable loginCredentials) {
    // Search for user by email first
    Optional<UserTable> user = userService.findByEmail(loginCredentials.getEmail());

    // If not found by email, try by phone number
    if (user.isEmpty()) {
        user = userService.findByPhoneNumber(loginCredentials.getPhoneNumber());
    }

    // If user is found
    if (user.isPresent()) {
        UserTable foundUser = user.get();

        // Check password - compare raw password with stored password
        boolean passwordMatches = userService.checkPassword(loginCredentials.getPassword(), foundUser.getPassword());

        if (passwordMatches) {
            // Bypass checks for admin role - admin doesn't need to go through the 'isAccept' check
            if ("Admin".equals(foundUser.getRole())) {
                String token = jwtUtil.generateToken(foundUser.getName(), foundUser.getEmail());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login Successful");
                response.put("token", token);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            // Check if isAccept is Pending or Declined for non-admin users
            if ("Declined".equals(foundUser.getAccept())) {
                return new ResponseEntity<>(
                        Map.of("message", "Your account is either pending or declined. Please contact the admin."),
                        HttpStatus.FORBIDDEN);
            }

            // If the user is a surveyor and their isAccept is not "Accepted", deny access
            if ("surveyor".equals(foundUser.getRole()) && !"Accepted".equals(foundUser.getAccept())) {
                return new ResponseEntity<>(
                        Map.of("message", "Your access is denied. Please contact the admin to get access."),
                        HttpStatus.FORBIDDEN);
            }

            // Proceed with normal user login
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

   @PutMapping("/reset-password")
public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestBody String newPasswordJson) {
    // Manually parse the JSON string to extract the password value
    String password = "";
    try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(newPasswordJson);
        JsonNode passwordNode = rootNode.get("newPassword");
        if (passwordNode != null) {
            password = passwordNode.asText();
        }
    } catch (Exception e) {
        System.out.println("Error parsing new password JSON: " + e.getMessage());
        return new ResponseEntity<>("Invalid password format.", HttpStatus.BAD_REQUEST);
    }

    Optional<UserTable> user = userService.findByEmail(email);
    if (user.isPresent()) {
        UserTable foundUser = user.get();
        
        // Save the plain text password directly
        foundUser.setPassword(password);
        userService.saveUser(foundUser);
        
        return new ResponseEntity<>("Password successfully updated.", HttpStatus.OK);
    } else {
        return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
    }
}
    /**
     * Getting user details controller use the token i set her and store the
     * response in the cache while login and pass through all the screen and
     * make sure the cache is not destroyed when the app is refreshed only needs
     * to destroyed when the user logged out or session is expiered use the
     * error status i used in the response to show in the front end
     *
     */
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
                response.put("constituency", foundUser.getConstituency());
                response.put("role", foundUser.getRole());

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
     * Admin access controller for accepting user login pass the email as the
     * params and pass the autherization in the header like this key :
     * autherization , value : Bearer <token>
     *
     */
    @PutMapping("/activate-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String email) {
        String token = authorizationHeader.replace("Bearer ", "");

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        // Fetch user by email
        Optional<UserTable> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserTable user = userOptional.get();

            // Check if user is already activated
            if ("Accepted".equals(user.getAccept())) {
                return ResponseEntity.ok("User with email " + email + " is already activated.");
            }

            // Update user status to "Accepted"
            user.setAccept("Accepted");
            userRepository.save(user);

            // Send activation email
            emailService.sendActivationEmail(user.getEmail(), user.getName());

            return ResponseEntity.ok("User with email " + email + " has been activated and notified.");
        } else {
            return ResponseEntity.badRequest().body("User with email " + email + " not found.");
        }
    }

    /**
     * Admin access controller for declining a user
     */
    @PutMapping("/decline-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> declineUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String email) {
        String token = authorizationHeader.replace("Bearer ", "");

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        // Fetch user by email
        Optional<UserTable> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            UserTable user = userOptional.get();

            // Check if user is already declined
            if ("Declined".equals(user.getAccept())) {
                return ResponseEntity.ok("User with email " + email + " is already declined.");
            }

            // Update user status to "Declined"
            user.setAccept("Declined");
            userRepository.save(user);

            // Send decline email
            emailService.sendDeclineEmail(user.getEmail(), user.getName());

            return ResponseEntity.ok("User with email " + email + " has been declined and notified.");
        } else {
            return ResponseEntity.badRequest().body("User with email " + email + " not found.");
        }
    }

}
