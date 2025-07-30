package com.survey.esa.fileUpload;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "filedata")
public class FIledata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Long id;
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
    private boolean voted = false;  // Default value for voted field
    public FIledata() {
    }
    public FIledata(String assemblyConstituency, String booth, String section, String serialNumber, String voterID,
                    String name, String relationType, String relationName, String houseNumber, String age, String gender, boolean voted) {
        this.assemblyConstituency = assemblyConstituency;
        this.booth = booth;
        this.section = section;
        this.serialNumber = serialNumber;
        this.voterID = voterID;
        this.voted = voted;  // Set the voted field
        this.name = name;
        this.relationType = relationType;
        this.relationName = relationName;
        this.houseNumber = houseNumber;
        this.age = age;
        this.gender = gender;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public boolean isVoted() {
        return voted;
    }   
    public void setVoted(boolean voted) {
        this.voted = voted;
    }
}
