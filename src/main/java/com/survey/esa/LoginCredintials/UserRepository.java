package com.survey.esa.LoginCredintials;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserTable, Long> {
    Optional<UserTable> findByEmail(String email);

    Optional<UserTable> findByPhoneNumber(String phoneNumber); 
}
