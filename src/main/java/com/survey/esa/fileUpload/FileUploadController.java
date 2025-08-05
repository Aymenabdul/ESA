package com.survey.esa.fileUpload;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.survey.esa.jwttoken.JwtUtil;

@RestController
@RequestMapping("api2/file")
public class FileUploadController {

    @Autowired
    private FileDataService fileDataService;

    @Autowired
    private FileDataRepository filedataRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload")
    @SuppressWarnings("CallToPrintStackTrace")
    public String uploadFiles(@RequestParam("file") MultipartFile[] files, @RequestParam("surveyName") String surveyName,
            @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7)
                : authorizationHeader;
        if (jwtToken == null || jwtToken.isEmpty()) {
            return "Missing or invalid token!";
        }
        if (!jwtUtil.validateToken(jwtToken)) {
            return "Invalid or expired token!";
        }
        String username = jwtUtil.extractUsername(jwtToken);
        if (username == null) {
            return "Failed to extract username from token!";
        }
        System.out.println("Authenticated user: " + username);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<String>> results = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                results.add(executor.submit(() -> processFile(file, surveyName)));
            }
            for (Future<String> result : results) {
                String message = result.get();
                if (!"File uploaded successfully!".equals(message)) {
                    return message;
                }
            }
            return "All files uploaded successfully!";
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Error processing files!";
        } finally {
            executor.shutdown();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private String processFile(MultipartFile file, String surveyName) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<FIledata> fileDataList = new ArrayList<>();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return "Invalid file format. No header row found.";
            }
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                columnIndexMap.put(headerRow.getCell(i).getStringCellValue().trim(), i);
            }
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
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

                FIledata fileData = new FIledata(
                        surveyName, assemblyConstituency, booth, section, serialNumber, voterID, name,
                        relationType, relationName, houseNumber, age, gender, false, false, null
                );
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

            return "File uploaded successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed due to IO error!";
        }
    }

    private String getCellValue(Row row, Map<String, Integer> columnIndexMap, String columnName) {
        Integer columnIndex = columnIndexMap.get(columnName);
        if (columnIndex != null) {
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                return "";
            }
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue()).replaceAll("\\.0$", ""); // Remove .0 for whole numbers
                }
            } else {
                return cell.toString().trim();
            }
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

    @GetMapping("/survey-stats")
    public List<Map<String, Object>> getSurveyStats(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String jwtToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        if (jwtToken == null || jwtToken.isEmpty() || !jwtUtil.validateToken(jwtToken)) {
            throw new Exception("Invalid or expired token");
        }
        List<Object[]> results = filedataRepository.getSurveyStatsGroupedBySurveyName();
        List<Map<String, Object>> formattedResults = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> surveyStat = new HashMap<>();
            String surveyName = (String) result[0];
            Long constituencyCount = (Long) result[1];
            Long boothCount = (Long) result[2];
            Timestamp createdAtTimestamp = (Timestamp) result[3];
            Boolean isActive = (Boolean) result[4];
            LocalDateTime createdAt = createdAtTimestamp.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            String formattedDate = createdAt.format(formatter);
            surveyStat.put("surveyName", surveyName);
            surveyStat.put("constituency", constituencyCount);
            surveyStat.put("booth", boothCount);
            surveyStat.put("createdAt", formattedDate);
            surveyStat.put("isActive", isActive);
            formattedResults.add(surveyStat);
        }

        return formattedResults;
    }

    @PutMapping("/activate-survey")
    public ResponseEntity<String> activateSurvey(
            @RequestParam("surveyName") String surveyName, 
            @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        if (jwtToken == null || jwtToken.isEmpty() || !jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        boolean isActivated = fileDataService.activateSurvey(surveyName);
        if (isActivated) {
            return ResponseEntity.ok("Survey data activated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Survey not found or no data to activate.");
        }
    }

    @PutMapping("/deactivate-survey")
    public ResponseEntity<String> deactivateSurvey(
            @RequestParam("surveyName") String surveyName, 
            @RequestHeader("Authorization") String authorizationHeader) {
        String jwtToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        if (jwtToken == null || jwtToken.isEmpty() || !jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        boolean isDeactivated = fileDataService.deactivateSurvey(surveyName);
        if (isDeactivated) {
            return ResponseEntity.ok("Survey data deactivated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Survey not found or no data to deactivate.");
        }
    }

//..........................................................................................................................................
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
            return ResponseEntity.ok(statusList); // Return the list of "voted" or "not voted"
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // If no records found
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
