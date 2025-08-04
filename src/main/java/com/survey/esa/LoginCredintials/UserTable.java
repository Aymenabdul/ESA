package com.survey.esa.LoginCredintials;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "users")
public class UserTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String role;
    private String constituency;
    private String phoneNumber;
    private String password;

    // Use OffsetDateTime to store the date, time, and offset (e.g., +05:30)
    // You can also use LocalDateTime, but this is more explicit about timezones.
    private OffsetDateTime createdAt;
    
    @Column(name = "is_accepted")
    private boolean isAccept = false;

    // This method will be automatically called by JPA right before an entity is first saved to the database.
    @PrePersist
    public void prePersist() {
        // Set the createdAt field to the current time in the Asia/Kolkata timezone
        this.createdAt = OffsetDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public UserTable() {
    }
    
    // ... (rest of your constructors and getters/setters remain the same)
    // You can remove the createdAt field from your constructor as it will be set automatically.
    
    public UserTable(Long id, String name, String email, String role, String constituency, String phoneNumber,
            String password, boolean isAccept) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.constituency = constituency;
        this.phoneNumber = phoneNumber;
        this.isAccept = isAccept;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}