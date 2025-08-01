package com.survey.esa.fileUpload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileDataService {

    @Autowired
    private FileDataRepository fileDataRepository; // Repository to persist the data

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
        return fileDataRepository.findAll(); // Assuming you're using JPA repository for database interaction
    }

   public List<FIledata> getFilteredData(String assemblyConstituency, String name, String houseNumber, 
                                       String serialNumber, String booth) {
    // Trim and normalize booth (case-insensitive and remove spaces)
    if (booth != null && !booth.isEmpty()) {
        booth = booth.trim().toLowerCase();  // Normalize and trim booth
    }

    // Debugging output to see the received parameters
    System.out.println("Assembly Constituency: " + assemblyConstituency);
    System.out.println("Name: " + name);
    System.out.println("House Number: " + houseNumber);
    System.out.println("Serial Number: " + serialNumber);
    System.out.println("Booth: " + booth);

    // Call the repository method to apply the filters
    return fileDataRepository.findFilteredData(assemblyConstituency, name, houseNumber, 
                                               serialNumber, booth);
}





    public Optional<FIledata> getFileDataById(Long id) {
        return fileDataRepository.findById(id);
    }

    public boolean updateVotedStatus(Long id) {
        Optional<FIledata> fileDataOptional = fileDataRepository.findById(id);

        if (fileDataOptional.isPresent()) {
            FIledata fileData = fileDataOptional.get();
            if (!fileData.isVoted()) { // Only update if it's not already voted
                fileData.setVoted(true);
                fileDataRepository.save(fileData);
                return true;
            }
        }
        return false;
    }

    public List<String> getAllVotedStatus() {
        List<FIledata> fileDataList = fileDataRepository.findAll(); // Retrieve all records

        // Create a list to store the string representation of the voted status
        List<String> statusList = new ArrayList<>();
        for (FIledata fileData : fileDataList) {
            // Add "voted" or "not voted" to the list based on the 'voted' field
            statusList.add(fileData.isVoted() ? "voted" : "not voted");
        }
        return statusList;
    }

    public List<String> getDistinctBooths() {
        return fileDataRepository.findDistinctBooths();
    }


    public long getTotalConstituencies() {
        return fileDataRepository.countDistinctConstituencies();
    }

    public long getTotalBooths(String constituency) {
        if (constituency == null || constituency.isEmpty()) {
            return fileDataRepository.countAllBooths();  // Return count of all booths
        } else {
            return fileDataRepository.countBoothsByConstituency(constituency);  // Filter by constituency
        }
    }

     public long getTotalVoters(String constituency, String booth) {
        if (constituency != null && !constituency.isEmpty() && booth != null && !booth.isEmpty()) {
            return fileDataRepository.countVotersByConstituencyAndBooth(constituency, booth);
        } else if (constituency != null && !constituency.isEmpty()) {
            return fileDataRepository.countVotersByConstituency(constituency);  // Filter by constituency only
        } else if (booth != null && !booth.isEmpty()) {
            return fileDataRepository.countVotersByBooth(booth);  // Filter by booth only
        } else {
            return fileDataRepository.countAllVoters();  // No filter, count all voters
        }
    }

}
