package com.survey.esa.fileUpload;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FIledata, Long> {
    List<FIledata> findByAssemblyConstituency(String assemblyConstituency);
  
    @Query(value = "SELECT DISTINCT assembly_constituency FROM filedata", nativeQuery = true)
    List<String> findDistinctAssemblyConstituency();

   @Query(value = "SELECT * FROM file_data f WHERE f.assembly_constituency = ?1 AND f.name = ?2 AND f.house_number = ?3 AND f.serial_number = ?4 AND f.booth = ?5 AND f.district = ?6", nativeQuery = true)
    List<FIledata> findFilteredData(String assemblyConstituency, String name, String houseNumber, String serialNumber, String booth, String district);

}
