package com.survey.esa.fileUpload;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "filedata")
public class FIledata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String surveyName;
    private String assemblyConstituency;
    private String booth;
    private String section;
    private String serialNumber;
    private String voterID;
    private String name;
    private String relationType;
    private String relationName;
    private String houseNumber;
    private String age;
    private String gender;
    private LocalDateTime createAt;
    private boolean isActive = false;
    private boolean voted = false;
    private boolean verified = false;

    public FIledata() {
    }

    public FIledata(String surveyName, String assemblyConstituency, String booth, String section,
            String serialNumber, String voterID, String name, String relationType,
            String relationName, String houseNumber, String age, String gender,
            boolean isActive, boolean voted,boolean verified, LocalDateTime createAt) {
        this.surveyName = surveyName;
        this.assemblyConstituency = assemblyConstituency;
        this.booth = booth;
        this.section = section;
        this.serialNumber = serialNumber;
        this.voterID = voterID;
        this.name = name;
        this.createAt = createAt;
        this.relationType = relationType;
        this.relationName = relationName;
        this.houseNumber = houseNumber;
        this.age = age;
        this.gender = gender;
        this.isActive = isActive;
        this.voted = voted;
        this.verified=verified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getAssemblyConstituency() {
        return assemblyConstituency;
    }

    public void setAssemblyConstituency(String assemblyConstituency) {
        this.assemblyConstituency = assemblyConstituency;
    }

    public String getBooth() {
        return booth;
    }

    public void setBooth(String booth) {
        this.booth = booth;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getVoterID() {
        return voterID;
    }

    public void setVoterID(String voterID) {
        this.voterID = voterID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @PrePersist
    public void prePersist() {
        if (this.createAt == null) {
            // Get current time in Asia/Kolkata timezone
            ZonedDateTime indiaTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

            // Truncate to hours and minutes only by using `truncatedTo` method
            this.createAt = indiaTime.toLocalDateTime().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        }
    }

    // Custom method to get formatted date and time in 12-hour format
    public String getFormattedCreateAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"); // 12-hour format with AM/PM
        return this.createAt.format(formatter);  // Return formatted date and time
    }
}
