package com.survey.esa.fileUpload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileDataService {

    @Autowired
    private FileDataRepository fileDataRepository;  // Repository to persist the data

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
}
