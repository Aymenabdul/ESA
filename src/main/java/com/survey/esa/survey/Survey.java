package com.survey.esa.survey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "filedata_id")
    private String fileDataId;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String Voter_type;
    private boolean isVerified = true ;
    @Column(name = "booth")
    private String booth;
    @Column(name = "constituency")
    private String constituency;
    @Column(name = "house_number")
    private String houseNumber;
    @Column(name = "gender")
    private String gender;
    @Column(name = "name")
    private String name;
    @Column(name = "voter_id")
    private String voterId;
    @Column(name = "VoterStatus")
    private String VoterStatus;
    @Column(name = "WhatsappNumber")
    private String WhatsappNumber;
    @Column(name = "ques1")
    private String ques1;
    @Column(name = "ques2")
    private String ques2;
    @Column(name = "ques3")
    private String ques3;
    @Column(name = "ques4")
    private String ques4;
    @Column(name = "ques5")
    private String ques5;
    @Column(name = "ques6")
    private String ques6;
    @JsonFormat(pattern = "hh:mm a dd-MMM-yyyy", timezone = "Asia/Kolkata")
    private LocalDateTime createdAt;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("updated_by")
    private String updatedBy;
    @JsonFormat(pattern = "hh:mm a dd-MMM-yyyy", timezone = "Asia/Kolkata")
    private LocalDateTime updatedDate;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "survey_name") 
    private String surveyName;
    @Column(name="role")
    private String role;

    public Survey() {
    }

    public Survey(Long id, String fileDataId, String phoneNumber, String voter_type, boolean isVerified, String booth,
            String constituency, String houseNumber, String gender, String name, String voterId, String voterStatus,
            String whatsappNumber, String ques1, String ques2, String ques3, String ques4, String ques5, String ques6,
            LocalDateTime createdAt, String createdBy, String updatedBy, LocalDateTime updatedDate, String surveyName,Long userId,String role) {
        this.id = id;
        this.fileDataId = fileDataId;
        this.role=role;
        this.phoneNumber = phoneNumber;
        this.Voter_type = voter_type;
        this.userId = userId;
        this.isVerified = isVerified;
        this.booth = booth;
        this.constituency = constituency;
        this.houseNumber = houseNumber;
        this.gender = gender;
        this.name = name;
        this.voterId = voterId;
        this.VoterStatus = voterStatus;
        this.WhatsappNumber = whatsappNumber;
        this.ques1 = ques1;
        this.ques2 = ques2;
        this.ques3 = ques3;
        this.ques4 = ques4;
        this.ques5 = ques5;
        this.ques6 = ques6;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
        this.surveyName = surveyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(String fileDataId) {
        this.fileDataId = fileDataId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVoter_type() {
        return Voter_type;
    }

    public void setVoter_type(String voter_type) {
        Voter_type = voter_type;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getBooth() {
        return booth;
    }

    public void setBooth(String booth) {
        this.booth = booth;
    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getVoterStatus() {
        return VoterStatus;
    }

    public void setVoterStatus(String voterStatus) {
        VoterStatus = voterStatus;
    }

    public String getWhatsappNumber() {
        return WhatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        WhatsappNumber = whatsappNumber;
    }

    public String getQues1() {
        return ques1;
    }

    public void setQues1(String ques1) {
        this.ques1 = ques1;
    }

    public String getQues2() {
        return ques2;
    }

    public void setQues2(String ques2) {
        this.ques2 = ques2;
    }

    public String getQues3() {
        return ques3;
    }

    public void setQues3(String ques3) {
        this.ques3 = ques3;
    }

    public String getQues4() {
        return ques4;
    }

    public void setQues4(String ques4) {
        this.ques4 = ques4;
    }

    public String getQues5() {
        return ques5;
    }

    public void setQues5(String ques5) {
        this.ques5 = ques5;
    }

    public String getQues6() {
        return ques6;
    }

    public void setQues6(String ques6) {
        this.ques6 = ques6;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }

     @PrePersist
    public void prePersist() {
        ZonedDateTime kolkataTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.createdAt = kolkataTime.toLocalDateTime();
        this.updatedDate = kolkataTime.toLocalDateTime();
    }

    @PreUpdate
    public void preUpdate() {
        ZonedDateTime kolkataTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.updatedDate = kolkataTime.toLocalDateTime();
    }
   
}

