package com.survey.esa.fileUpload;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
                        relationType, relationName, houseNumber, age, gender, false, false,false, null
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

    @GetMapping("/active")
    public ResponseEntity<Object> getActiveSurveyNames() {
        List<String> activeSurveyNames = fileDataService.getActiveSurveyNames();
        Set<String> uniqueSurveyNames = new HashSet<>(activeSurveyNames);
        if (uniqueSurveyNames.isEmpty()) {
            return ResponseEntity.status(404).body("No active surveys found");  // Return custom message if no active surveys
        }
        return ResponseEntity.ok(uniqueSurveyNames);
    }

    @GetMapping("/distinct-constituencies")
    public List<String> getDistinctAssemblyConstituencies(@RequestParam("surveyName") String surveyName) {
        return fileDataService.getDistinctAssemblyConstituenciesBySurveyName(surveyName);
    }

    @GetMapping("/constituencies")
    public List<String> getDistinctConstituencies() {
        return fileDataService.getDistinctAssemblyConstituencies();
    }

    @GetMapping("/distinct-booths")
    public ResponseEntity<List<String>> getDistinctBooths(
            @RequestParam("surveyName") String surveyName,
            @RequestParam("Constituency") String assemblyConstituency) {

        List<String> booths = fileDataService.getDistinctBoothsBySurveyNameAndConstituency(surveyName, assemblyConstituency);
        return ResponseEntity.ok(booths);
    }

    @GetMapping("/filter2")
    public List<FIledata> getFilteredData(
            @RequestParam("surveyName") String surveyName,
            @RequestParam("Constituency") String assemblyConstituency,
            @RequestParam("booth") String booth,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "houseNumber", required = false) String houseNumber,
            @RequestParam(value = "serialNumber", required = false) String serialNumber) {

        return fileDataService.getFilteredData(
                surveyName,
                assemblyConstituency,
                booth,
                name,
                houseNumber,
                serialNumber
        );
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
            return ResponseEntity.ok("Voted status toggled successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found.");
        }
    }

    @PutMapping("/markAsVerified/{id}")
public ResponseEntity<String> markAsVerified(@PathVariable Long id) {
    boolean updated = fileDataService.updateVerifiedStatus(id); // Update the verified status

    if (updated) {
        return ResponseEntity.ok("Verified status toggled successfully.");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found.");
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
    public ResponseEntity<Long> getTotalConstituencies(@RequestParam("surveyName") String surveyName) {
        long totalConstituencies = fileDataService.getTotalConstituenciesBySurveyName(surveyName);
        return ResponseEntity.ok(totalConstituencies);
    }

    @GetMapping("/total-booths")
    public ResponseEntity<Long> getTotalBooths(
            @RequestParam("surveyName") String surveyName,
            @RequestParam("Constituency") String constituency) {
        long totalBooths = fileDataService.getTotalBoothsBySurveyNameAndConstituency(surveyName, constituency);
        return ResponseEntity.ok(totalBooths);
    }

    @GetMapping("/filter-counts")
    public ResponseEntity<Map<String, Long>> getSurveyCounts(
            @RequestParam(value = "surveyName", required = false) String surveyName, // Optional
            @RequestParam(value = "constituency", required = false) String constituency, // Optional
            @RequestParam(value = "booth", required = false) String booth // Optional
    ) {
        // If no surveyName is provided, return counts as 0
        if (surveyName == null || surveyName.isEmpty()) {
            Map<String, Long> counts = new HashMap<>();
            counts.put("constituencyCount", 0L);
            counts.put("boothCount", 0L);
            counts.put("voterCount", 0L);
            return ResponseEntity.ok(counts);
        }
        Map<String, Long> counts = null;
        if (constituency != null && booth != null) {
            counts = fileDataService.getCountsBySurveyNameAndConstituencyAndBooth(surveyName, constituency, booth);
        } else if (constituency != null) {
            counts = fileDataService.getCountsBySurveyNameAndConstituency(surveyName, constituency);
        } else {
            counts = fileDataService.getCountsBySurveyName(surveyName);
        }

        // If no counts found, return a 404 status
        if (counts == null || counts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(counts); // Return the counts as a map
    }

}
