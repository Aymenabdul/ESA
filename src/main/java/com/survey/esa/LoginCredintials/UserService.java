package com.survey.esa.LoginCredintials;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    // The PasswordEncoder dependency is no longer needed
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("UserService initialized with UserRepository.");
    }

    public UserTable saveUser(UserTable userTable) {
        logger.info("Attempting to save user with email: {}", userTable.getEmail());
        try {
            // The password is saved as plain text
            UserTable savedUser = userRepository.save(userTable);
            logger.info("User with email: {} saved successfully with a plain text password.", userTable.getEmail());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error saving user with email: {}", userTable.getEmail(), e);
            throw e;
        }
    }

    public Optional<UserTable> findByEmail(String email) {
        logger.debug("Searching for user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public Optional<UserTable> findByPhoneNumber(String phoneNumber) {
        logger.debug("Searching for user by phone number: {}", phoneNumber);
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    // Passwords are now checked by direct comparison
    public boolean checkPassword(String rawPassword, String storedPassword) {
        logger.debug("Checking password for a user using plain text comparison.");
        boolean passwordMatches = rawPassword.equals(storedPassword);
        logger.info("Password check result: {}", passwordMatches);
        return passwordMatches;
    }

    public List<UserTable> getAllUsers() {
        logger.debug("Fetching all users.");
        return userRepository.findAll();
    }
}