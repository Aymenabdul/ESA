package com.survey.esa.fileUpload;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api2/file")
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
            if (headerRow == null) {
                return "Invalid file format. No header row found.";
            }
            
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                columnIndexMap.put(headerRow.getCell(i).getStringCellValue().trim(), i); // Trim to handle extra spaces
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

                // Set `voted` field to false by default
                FIledata fileData = new FIledata(assemblyConstituency, booth, section, serialNumber, voterID, name, relationType, relationName, houseNumber, age, gender, false);
                if (isValid(fileData)) {
                    fileDataList.add(fileData);
                } else {
                    System.out.println("Skipping invalid data: " + voterID);
                }
            }

            if (!fileDataList.isEmpty()) {
                fileDataService.saveFileData(fileDataList);
            } else {
                System.out.println("No valid data to save!");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "Error processing file data. Invalid number format.";
        }

        return "File uploaded successfully!";
    } catch (IOException e) {
        e.printStackTrace();
        return "File upload failed due to IO error!";
    }
}

// Utility method to handle different cell types and prevent decimal values for integers
private String getCellValue(Row row, Map<String, Integer> columnIndexMap, String columnName) {
    Integer columnIndex = columnIndexMap.get(columnName);  
    if (columnIndex != null) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";  // Return empty string if cell is null
        }

        // Handle numeric values by checking if the cell is a whole number (i.e., integer)
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                // If the cell contains a date, convert it to a string
                return cell.getDateCellValue().toString();
            } else {
                // For numeric (float or double), check if it’s a whole number
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);  // Convert to long and return as string (whole number)
                } else {
                    return String.valueOf(numericValue);  // Return decimal values as strings
                }
            }
        } else {
            // For other types (string, boolean), return the value as a string
            return cell.toString().trim();
        }
    }
    return "";  // Return empty string if the columnIndex is not found
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

     @GetMapping("/distinct-booths")
    public ResponseEntity<List<String>> getDistinctBooths() {
        List<String> booths = fileDataService.getDistinctBooths();
        return ResponseEntity.ok(booths);
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

@GetMapping("/filter2")
public List<FIledata> getFilteredData(
    @RequestParam("assemblyConstituency") String assemblyConstituency,
    @RequestParam(value = "name", required = false) String name,
    @RequestParam(value = "houseNumber", required = false) String houseNumber,
    @RequestParam(value = "serialNumber", required = false) String serialNumber,
    @RequestParam(value = "booth", required = false) String booth) {

    return fileDataService.getFilteredData(assemblyConstituency, name, houseNumber, serialNumber, booth);
}
 @GetMapping("/getFileData/{id}")
    public Optional<FIledata> getFileDataById(@PathVariable Long id) {
        Optional<FIledata> fileData = fileDataService.getFileDataById(id);
        
        if (fileData.isEmpty()) {
            System.out.println("No data found with ID: " + id);
        } else {
            System.out.println("Fetched File Data: ");
            FIledata data = fileData.get();
            System.out.println("Voter ID: " + data.getVoterID() + ", Name: " + data.getName());
        }

        return fileData;
    }

@PutMapping("/markAsVoted/{id}")
    public ResponseEntity<String> markAsVoted(@PathVariable Long id) {
        boolean updated = fileDataService.updateVotedStatus(id);
        
        if (updated) {
            return ResponseEntity.ok("Voted status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found or already voted.");
        }
    }
@GetMapping("/getAllVotedStatus")
    public ResponseEntity<List<String>> getAllVotedStatus() {
        List<String> statusList = fileDataService.getAllVotedStatus();
        
        if (!statusList.isEmpty()) {
            return ResponseEntity.ok(statusList);  // Return the list of "voted" or "not voted"
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // If no records found
        }
    }

    @GetMapping("/total-constituencies")
    public ResponseEntity<Long> getTotalConstituencies() {
        long totalConstituencies = fileDataService.getTotalConstituencies();
        return ResponseEntity.ok(totalConstituencies);
    }

 @GetMapping("/total-booths")
    public ResponseEntity<Long> getTotalBooths(@RequestParam(required = false) String constituency) {
        long totalBooths = fileDataService.getTotalBooths(constituency);
        return ResponseEntity.ok(totalBooths);
    }

   @GetMapping("/total-voters")
    public ResponseEntity<Long> getTotalVoters(
        @RequestParam(required = false) String constituency,
        @RequestParam(required = false) String booth) {
        
        // Call the service to get the filtered or total voter count
        long totalVoters = fileDataService.getTotalVoters(constituency, booth);
        
        // Return the count as a response
        return ResponseEntity.ok(totalVoters);
    }
    
}
