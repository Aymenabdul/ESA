package com.survey.esa.fileUpload;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FileDataService fileDataService;

    @PostMapping("/upload")
public String uploadFile(@RequestParam("file") MultipartFile file) {
    try {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<FIledata> fileDataList = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                columnIndexMap.put(headerRow.getCell(i).getStringCellValue().trim(), i);  // Added .trim() to handle any extra spaces
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                String assemblyConstituency = getCellValue(row, columnIndexMap, "Assembly Constituency");
                String booth = getCellValue(row, columnIndexMap, "Part No");
                String section = getCellValue(row, columnIndexMap, "Section");
                String serialNumber = getCellValue(row, columnIndexMap, "Serial Number");
                String voterID = getCellValue(row, columnIndexMap, "Voter ID");
                String name = getCellValue(row, columnIndexMap, "Name");
                String relationType = getCellValue(row, columnIndexMap, "Relation Type");
                String relationName = getCellValue(row, columnIndexMap, "Relation Name");
                String houseNumber = getCellValue(row, columnIndexMap, "House Number");
                String age = getCellValue(row, columnIndexMap, "Age");
                String gender = getCellValue(row, columnIndexMap, "Gender");

                System.out.println("Extracted Data: ");
                System.out.println("Assembly Constituency: " + assemblyConstituency);
                System.out.println("Booth: " + booth);
                System.out.println("Section: " + section);
                System.out.println("Serial Number: " + serialNumber);
                System.out.println("Voter ID: " + voterID);
                System.out.println("Name: " + name);
                System.out.println("Relation Type: " + relationType);
                System.out.println("Relation Name: " + relationName);
                System.out.println("House Number: " + houseNumber);
                System.out.println("Age: " + age);
                System.out.println("Gender: " + gender);

                FIledata fileData = new FIledata(assemblyConstituency, booth, section, serialNumber, voterID, name, relationType, relationName, houseNumber, age, gender);
                if (isValid(fileData)) {
                    fileDataList.add(fileData);
                } else {
                    System.out.println("Skipping invalid data: " + voterID);
                }
            }

            if (!fileDataList.isEmpty()) {
                fileDataService.saveFileData(fileDataList);
            } else {
                System.out.println("No data to save!");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return "File uploaded successfully!";
    } catch (IOException e) {
        e.printStackTrace();
        return "File upload failed!";
    }
}

private String getCellValue(Row row, Map<String, Integer> columnIndexMap, String columnName) {
    Integer columnIndex = columnIndexMap.get(columnName);  
    if (columnIndex != null) {
        return row.getCell(columnIndex) != null ? row.getCell(columnIndex).toString() : "";
    }
    return "";  
}

private boolean isValid(FIledata fileData) {
    if (fileData.getName().isEmpty() || fileData.getVoterID().isEmpty()) {
        System.out.println("Skipping invalid data: " + fileData.getVoterID());
        return false;
    }
    return true;
}

@GetMapping("/filter")
    public List<FIledata> getFilteredData(@RequestParam("assemblyConstituency") String assemblyConstituency) {
        return fileDataService.getDataByAssemblyConstituency(assemblyConstituency);
    }  
    
    @GetMapping("/distinct-constituencies")
    public List<String> getDistinctAssemblyConstituencies() {
        return fileDataService.getDistinctAssemblyConstituencies();
    }

    @GetMapping("/getFileData")
public List<FIledata> getFileData() {
    List<FIledata> fileDataList = fileDataService.getAllFileData();
    if (fileDataList.isEmpty()) {
        System.out.println("No data found!");
    } else {
        System.out.println("Fetched File Data: ");
        fileDataList.forEach(data -> {
            System.out.println("Voter ID: " + data.getVoterID() + ", Name: " + data.getName());
        });
    }
    return fileDataList;
}

}
