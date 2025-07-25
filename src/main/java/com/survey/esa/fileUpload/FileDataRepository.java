package com.survey.esa.fileUpload;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FIledata, Long> {
    List<FIledata> findByAssemblyConstituency(String assemblyConstituency);
  
    @Query(value = "SELECT DISTINCT assembly_constituency FROM filedata", nativeQuery = true)
    List<String> findDistinctAssemblyConstituency();
}
