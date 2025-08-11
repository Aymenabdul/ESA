package com.survey.esa.fileUpload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileDataService {

    @Autowired
    private FileDataRepository fileDataRepository; // Repository to persist the data

    public void saveFileData(List<FIledata> fileDataList) {
        if (fileDataList.isEmpty()) {
            System.out.println("No data to save!");
        } else {
            System.out.println("Saving " + fileDataList.size() + " records to the database.");
            fileDataRepository.saveAll(fileDataList);
        }
    }

    public List<String> getActiveSurveyNames() {
        // Fetch surveys with isActive = true and return their survey names
        List<FIledata> activeSurveys = fileDataRepository.findByIsActiveTrue();
        return activeSurveys.stream()
                .map(FIledata::getSurveyName)
                .collect(Collectors.toList());
    }

    public List<FIledata> getDataByAssemblyConstituency(String assemblyConstituency) {
        return fileDataRepository.findByAssemblyConstituency(assemblyConstituency);
    }

    public List<String> getDistinctAssemblyConstituenciesBySurveyName(String surveyName) {
    return fileDataRepository.findDistinctAssemblyConstituenciesBySurveyName(surveyName);
}

    public List<FIledata> getFilteredData(
        String surveyName, 
        String assemblyConstituency, 
        String booth, 
        String name, 
        String houseNumber, 
        String serialNumber) {
    
    return fileDataRepository.findFilteredData(
            surveyName, 
            assemblyConstituency, 
            booth, 
            name, 
            houseNumber, 
            serialNumber
    );
}
    public Optional<FIledata> getFileDataById(Long id) {
        return fileDataRepository.findById(id);
    }

    public boolean updateVotedStatus(Long id) {
        Optional<FIledata> fileDataOptional = fileDataRepository.findById(id);

        if (fileDataOptional.isPresent()) {
            FIledata fileData = fileDataOptional.get();

            // Toggle the current voted status
            boolean currentStatus = fileData.isVoted();
            fileData.setVoted(!currentStatus);  // Flip the voted status

            // Save the updated fileData
            fileDataRepository.save(fileData);
            return true;
        }
        return false; // Return false if record not found
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

    public List<String> getDistinctBoothsBySurveyNameAndConstituency(String surveyName, String assemblyConstituency) {
    return fileDataRepository.findDistinctBoothsBySurveyNameAndConstituency(surveyName, assemblyConstituency);
}


   public long getTotalConstituenciesBySurveyName(String surveyName) {
    return fileDataRepository.countDistinctConstituenciesBySurveyName(surveyName);
}

    public long getTotalBoothsBySurveyNameAndConstituency(String surveyName, String constituency) {
    return fileDataRepository.countBoothsBySurveyNameAndConstituency(surveyName, constituency);
}

    // Get counts based only on the survey name
    public Map<String, Long> getCountsBySurveyName(String surveyName) {
        Map<String, Long> result = new HashMap<>();
        result.put("constituencyCount", fileDataRepository.countConstituenciesBySurveyName(surveyName));
        result.put("boothCount", fileDataRepository.countBoothsBySurveyName(surveyName));
        result.put("voterCount", fileDataRepository.countVotersBySurveyName(surveyName));
        return result;
    }

    // Get counts based on survey name and constituency
    public Map<String, Long> getCountsBySurveyNameAndConstituency(String surveyName, String constituency) {
        Map<String, Long> result = new HashMap<>();
        result.put("constituencyCount", fileDataRepository.countConstituenciesBySurveyNameAndConstituency(surveyName, constituency));
        result.put("boothCount", fileDataRepository.countBoothsBySurveyNameAndConstituency(surveyName, constituency));
        result.put("voterCount", fileDataRepository.countVotersBySurveyNameAndConstituency(surveyName, constituency));
        return result;
    }

    // Get counts based on survey name, constituency, and booth
    public Map<String, Long> getCountsBySurveyNameAndConstituencyAndBooth(String surveyName, String constituency, String booth) {
        Map<String, Long> result = new HashMap<>();
        result.put("constituencyCount", fileDataRepository.countConstituenciesBySurveyNameAndConstituencyAndBooth(surveyName, constituency, booth));
        result.put("boothCount", fileDataRepository.countBoothsBySurveyNameAndConstituencyAndBooth(surveyName, constituency, booth));
        result.put("voterCount", fileDataRepository.countVotersBySurveyNameAndConstituencyAndBooth(surveyName, constituency, booth));
        return result;
    }

    public boolean activateSurvey(String surveyName) {
        // Fetch all the records for the given surveyName
        List<FIledata> fileDataList = fileDataRepository.findBySurveyName(surveyName);

        if (fileDataList.isEmpty()) {
            return false; // No data found for the given survey name
        }

        // Set isActive to true for each record and save
        for (FIledata fileData : fileDataList) {
            fileData.setActive(true); // Activate the record
        }

        // Save all the updated records
        fileDataRepository.saveAll(fileDataList);
        return true;
    }

    public boolean deactivateSurvey(String surveyName) {
        // Fetch all the records for the given surveyName
        List<FIledata> fileDataList = fileDataRepository.findBySurveyName(surveyName);

        if (fileDataList.isEmpty()) {
            return false; // No data found for the given survey name
        }

        // Set isActive to false for each record and save
        for (FIledata fileData : fileDataList) {
            fileData.setActive(false); // Deactivate the record
        }

        // Save all the updated records
        fileDataRepository.saveAll(fileDataList);
        return true;
    }

    public List<String> getDistinctAssemblyConstituencies() {
        return fileDataRepository.findDistinctAssemblyConstituencies();
    }

    public boolean updateVerifiedStatus(Long id) {
    Optional<FIledata> fileDataOpt = fileDataRepository.findById(id); // Assuming you're using JPA

    if (fileDataOpt.isPresent()) {
        FIledata fileData = fileDataOpt.get();
        // Toggle the verified status (assuming it's a boolean field)
        fileData.setVerified(!fileData.getVerified());

        fileDataRepository.save(fileData); // Save the updated file data record
        return true;
    }
    return false; // Return false if the record wasn't found
}
}
