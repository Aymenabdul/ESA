package com.survey.esa.fileUpload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileDataService {

    @Autowired
    private FileDataRepository fileDataRepository;  // Repository to persist the data

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public void saveFileData(List<FIledata> fileDataList) {
    if (fileDataList.isEmpty()) {
        System.out.println("No data to save!");
    } else {
        System.out.println("Saving " + fileDataList.size() + " records to the database.");
        fileDataRepository.saveAll(fileDataList);
    }
}
 public List<FIledata> getDataByAssemblyConstituency(String assemblyConstituency) {
        return fileDataRepository.findByAssemblyConstituency(assemblyConstituency);
    }

      public List<String> getDistinctAssemblyConstituencies() {
        return fileDataRepository.findDistinctAssemblyConstituency();
    }

   public List<FIledata> getAllFileData() {
        return fileDataRepository.findAll();  // Assuming you're using JPA repository for database interaction
    }

    public List<FIledata> getFilteredData(String assemblyConstituency, String name, String houseNumber, String serialNumber, String boothNumber, String district) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<FIledata> cq = cb.createQuery(FIledata.class);
    Root<FIledata> root = cq.from(FIledata.class);

    List<Predicate> predicates = new ArrayList<>();

    // Adding filters to predicates list
    if (assemblyConstituency != null) {
        predicates.add(cb.equal(root.get("assemblyConstituency"), assemblyConstituency));
    }
    if (name != null) {
        predicates.add(cb.like(root.get("name"), "%" + name + "%"));
    }
    if (houseNumber != null) {
        predicates.add(cb.equal(root.get("houseNumber"), houseNumber));
    }
    if (serialNumber != null) {
        predicates.add(cb.equal(root.get("serialNumber"), serialNumber));
    }
    if (boothNumber != null) {
        predicates.add(cb.equal(root.get("boothNumber"), boothNumber));
    }
    if (district != null) {
        predicates.add(cb.equal(root.get("district"), district));
    }

    // Apply all filters
    cq.where(cb.and(predicates.toArray(new Predicate[0])));

    TypedQuery<FIledata> query = entityManager.createQuery(cq);
    return query.getResultList();
}

 public Optional<FIledata> getFileDataById(Long id) {
        return fileDataRepository.findById(id);
    }

}
