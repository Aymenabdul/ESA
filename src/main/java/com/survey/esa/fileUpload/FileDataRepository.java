package com.survey.esa.fileUpload;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileDataRepository extends JpaRepository<FIledata, Long> {

    List<FIledata> findByAssemblyConstituency(String assemblyConstituency);

    @Query("SELECT DISTINCT f.assemblyConstituency FROM FIledata f WHERE f.surveyName = :surveyName")
    List<String> findDistinctAssemblyConstituenciesBySurveyName(@Param("surveyName") String surveyName);

    @Query("SELECT DISTINCT f.booth FROM FIledata f WHERE f.surveyName = :surveyName AND f.assemblyConstituency = :assemblyConstituency")
    List<String> findDistinctBoothsBySurveyNameAndConstituency(
            @Param("surveyName") String surveyName,
            @Param("assemblyConstituency") String assemblyConstituency);

    @Query("SELECT f FROM FIledata f WHERE f.surveyName = :surveyName AND f.assemblyConstituency = :assemblyConstituency AND f.booth = :booth "
            + "AND (:name IS NULL OR f.name LIKE %:name%) "
            + "AND (:houseNumber IS NULL OR f.houseNumber LIKE %:houseNumber%) "
            + "AND (:serialNumber IS NULL OR f.serialNumber LIKE %:serialNumber%)")
    List<FIledata> findFilteredData(
            @Param("surveyName") String surveyName,
            @Param("assemblyConstituency") String assemblyConstituency,
            @Param("booth") String booth,
            @Param("name") String name,
            @Param("houseNumber") String houseNumber,
            @Param("serialNumber") String serialNumber
    );

    @Query(value = "SELECT COUNT(DISTINCT assembly_constituency) FROM filedata WHERE survey_name = :surveyName", nativeQuery = true)
    long countDistinctConstituenciesBySurveyName(@Param("surveyName") String surveyName);

    @Query(value = "SELECT COUNT(DISTINCT booth) FROM filedata WHERE survey_name = :surveyName AND assembly_constituency = :constituency", nativeQuery = true)
    long countBoothsBySurveyNameAndConstituency(@Param("surveyName") String surveyName, @Param("constituency") String constituency);

    @Query("SELECT COUNT(f.voterID) FROM FIledata f WHERE f.assemblyConstituency = :constituency AND f.booth = :booth")
    long countVotersByConstituencyAndBooth(@Param("constituency") String constituency, @Param("booth") String booth);

    //with token 
    @Query(value = "SELECT f.survey_name, "
            + "COUNT(DISTINCT f.assembly_constituency), "
            + "COUNT(DISTINCT f.booth), "
            + "MAX(f.create_at), "
            + "f.is_active "
            + "FROM filedata f "
            + "GROUP BY f.survey_name, f.is_active", nativeQuery = true)
    List<Object[]> getSurveyStatsGroupedBySurveyName();

    List<FIledata> findBySurveyName(String surveyName);

    List<FIledata> findByIsActiveTrue();

    // Count of constituencies based on surveyName
    @Query(value = "SELECT COUNT(DISTINCT f.assembly_constituency) FROM Filedata f WHERE f.survey_name = :surveyName", nativeQuery = true)
    long countConstituenciesBySurveyName(@Param("surveyName") String surveyName);

// Count of booths based on surveyName
    @Query(value = "SELECT COUNT(DISTINCT f.booth) FROM Filedata f WHERE f.survey_name = :surveyName", nativeQuery = true)
    long countBoothsBySurveyName(@Param("surveyName") String surveyName);

// Count of voters based on surveyName
    @Query(value = "SELECT COUNT(f.voterid) FROM Filedata f WHERE f.survey_name = :surveyName", nativeQuery = true)
    long countVotersBySurveyName(@Param("surveyName") String surveyName);

// Count of constituencies based on surveyName and constituency
    @Query(value = "SELECT COUNT(DISTINCT f.assembly_constituency) FROM Filedata f WHERE f.survey_name = :surveyName AND f.assembly_constituency = :constituency", nativeQuery = true)
    long countConstituenciesBySurveyNameAndConstituency(@Param("surveyName") String surveyName, @Param("constituency") String constituency);

// Count of voters based on surveyName and constituency
    @Query(value = "SELECT COUNT(f.voterid) FROM Filedata f WHERE f.survey_name = :surveyName AND f.assembly_constituency = :constituency", nativeQuery = true)
    long countVotersBySurveyNameAndConstituency(@Param("surveyName") String surveyName, @Param("constituency") String constituency);

// Count of constituencies based on surveyName, constituency, and booth
    @Query(value = "SELECT COUNT(DISTINCT f.assembly_constituency) FROM Filedata f WHERE f.survey_name = :surveyName AND f.assembly_constituency = :constituency AND (:booth IS NULL OR f.booth = :booth)", nativeQuery = true)
    long countConstituenciesBySurveyNameAndConstituencyAndBooth(@Param("surveyName") String surveyName, @Param("constituency") String constituency, @Param("booth") String booth);

// Count of booths based on surveyName, constituency, and booth
    @Query(value = "SELECT COUNT(DISTINCT f.booth) FROM Filedata f WHERE f.survey_name = :surveyName AND f.assembly_constituency = :constituency AND (:booth IS NULL OR f.booth = :booth)", nativeQuery = true)
    long countBoothsBySurveyNameAndConstituencyAndBooth(@Param("surveyName") String surveyName, @Param("constituency") String constituency, @Param("booth") String booth);

// Count of voters based on surveyName, constituency, and booth
    @Query(value = "SELECT COUNT(f.voterid) FROM Filedata f WHERE f.survey_name = :surveyName AND f.assembly_constituency = :constituency AND (:booth IS NULL OR f.booth = :booth)", nativeQuery = true)
    long countVotersBySurveyNameAndConstituencyAndBooth(@Param("surveyName") String surveyName, @Param("constituency") String constituency, @Param("booth") String booth);

}
