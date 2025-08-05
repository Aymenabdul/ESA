package com.survey.esa.fileUpload;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileDataRepository extends JpaRepository<FIledata, Long> {
  List<FIledata> findByAssemblyConstituency(String assemblyConstituency);

  @Query(value = "SELECT DISTINCT assembly_constituency FROM filedata", nativeQuery = true)
  List<String> findDistinctAssemblyConstituency();

  @Query("SELECT DISTINCT s.booth FROM Survey s")
  List<String> findDistinctBooths();

  @Query(value = "SELECT * FROM filedata f WHERE f.assembly_constituency = ?1 " +
      "AND (?2 IS NULL OR TRIM(f.name) LIKE TRIM(?2)) " + // Trim spaces for name
      "AND (?3 IS NULL OR TRIM(f.house_number) = TRIM(?3)) " + // Trim spaces for house_number
      "AND (?4 IS NULL OR TRIM(f.serial_number) = TRIM(?4)) " + // Trim spaces for serial_number
      "AND (?5 IS NULL OR TRIM(LOWER(f.booth)) = TRIM(LOWER(?5)))", nativeQuery = true)
  List<FIledata> findFilteredData(String assemblyConstituency, String name, String houseNumber,
      String serialNumber, String booth);

  @Query("SELECT COUNT(DISTINCT f.assemblyConstituency) FROM FIledata f")
  long countDistinctConstituencies();

  @Query("SELECT COUNT(DISTINCT f.booth) FROM FIledata f")
  long countAllBooths();

  @Query("SELECT COUNT(f.voterID) FROM FIledata f WHERE f.assemblyConstituency = :constituency AND f.booth = :booth")
  long countVotersByConstituencyAndBooth(@Param("constituency") String constituency, @Param("booth") String booth);

  // Get the count of voters filtered by constituency (all booths for that
  // constituency)
  @Query("SELECT COUNT(f.voterID) FROM FIledata f WHERE f.assemblyConstituency = :constituency")
  long countVotersByConstituency(@Param("constituency") String constituency);

  // Get the count of all voters (no filters)
  @Query("SELECT COUNT(f.voterID) FROM FIledata f")
  long countAllVoters();

  @Query("SELECT COUNT(DISTINCT f.booth) FROM FIledata f WHERE f.assemblyConstituency = :constituency")
  long countBoothsByConstituency(@Param("constituency") String constituency);


  //with token 

@Query(value = "SELECT f.survey_name, " +
               "COUNT(DISTINCT f.assembly_constituency), " +
               "COUNT(DISTINCT f.booth), " +
               "MAX(f.create_at), " +
               "f.is_active " +
               "FROM filedata f " +
               "GROUP BY f.survey_name, f.is_active", nativeQuery = true)
List<Object[]> getSurveyStatsGroupedBySurveyName();

List<FIledata> findBySurveyName(String surveyName);



}
