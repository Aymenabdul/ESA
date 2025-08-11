package com.survey.esa.survey;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api2/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyRepository surveyRepository;

    // Endpoint to submit a survey response
    @PostMapping("/submit")
    public Survey submitSurvey(@RequestBody Survey survey) {
        System.out.println("Controller reached. Survey received: " + survey);
        return surveyService.saveSurvey(survey);
    }

    @GetMapping("/getAllSurveys")
    public List<Survey> getAllSurveys() {
        return surveyService.getAllSurveys(); // Call the method in the service
    }

    @GetMapping("/voteCounts")
    public Map<String, Map<String, Long>> getSurveyVoteCounts() {
        return surveyService.getSurveyVoteCounts(); // Returns a Map for each question
    }

    @GetMapping("/filterBySurveyNameAndConstituency")
    public ResponseEntity<Map<String, Map<String, Long>>> getSurveyVoteCountsByFilter(
            @RequestParam("surveyName") String surveyName, // Make surveyName mandatory
            @RequestParam(value = "constituency", required = false) String constituency, // Optional
            @RequestParam(value = "booth", required = false) String booth) {  // Optional

        // Trim spaces and make the comparison case-insensitive for constituency and booth
        if (constituency != null) {
            constituency = constituency.trim().toLowerCase();
        }
        if (booth != null && !booth.isEmpty()) {
            booth = booth.trim().toLowerCase();  // Trim any extra spaces from booth number
        }

        Map<String, Map<String, Long>> voteCounts;

        try {
            // Call the service method to get the counts based on provided parameters
            if (constituency == null && booth == null) {
                // If both constituency and booth are not provided, filter only by surveyName
                voteCounts = surveyService.getSurveyVoteCountsFiltered(surveyName, null, null);
            } else if (booth == null || booth.isEmpty()) {
                // If only constituency is provided, filter by constituency and surveyName
                voteCounts = surveyService.getSurveyVoteCountsFiltered(surveyName, constituency, null);
            } else {
                // If both constituency and booth are provided, filter by all three parameters
                voteCounts = surveyService.getSurveyVoteCountsFiltered(surveyName, constituency, booth);
            }
            return ResponseEntity.ok(voteCounts);
        } catch (Exception e) {
            // Handle any exceptions (e.g., invalid constituency, booth, etc.)
            Map<String, Map<String, Long>> errorResponse = Collections.singletonMap(
                    "error", Collections.singletonMap("message", -1L));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/voters")
    public List<Survey> getAllVoters() {
        return surveyRepository.findAll(); // Returns all surveys, including their verification status
    }

   @GetMapping("/voters/{fileDataId}")
public ResponseEntity<Map<String, Object>> getSurveyStatus(@PathVariable String fileDataId) {
    System.out.println("fileDataId from URL: " + fileDataId); // Log the fileDataId

    // Fetch the survey based on fileDataId
    Survey survey = surveyRepository.findSurveyByFileDataId(fileDataId);

    // Handle the case where no survey is found (return 404 if null)
    if (survey == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found");
    }

    // Create the response map with only required fields
    Map<String, Object> response = new HashMap<>();
    response.put("isVerified", survey.isVerified());  // Include isVerified field
    response.put("id", survey.getId());  // Include id field

    return ResponseEntity.ok(response);
}

   @GetMapping("/votersbyId")
public List<Survey> getAllVoters(
        @RequestParam(value = "userId", required = false) Long userId, 
        @RequestParam(value = "surveyName", required = false) String surveyName
) {
    if (userId != null && surveyName != null) {
        // Fetch surveys with null voterId based on both userId and surveyName
        return surveyRepository.findByUserIdAndSurveyNameAndVoterIdIsNull(userId, surveyName);
    } else if (userId != null) {
        // Fetch surveys with null voterId based on userId
        return surveyRepository.findByUserIdAndVoterIdIsNull(userId);
    } else if (surveyName != null) {
        // Fetch surveys with null voterId based on surveyName
        return surveyRepository.findBySurveyNameAndVoterIdIsNull(surveyName);
    } else {
        // Fetch all surveys with null voterId
        return surveyRepository.findByVoterIdIsNull();
    }
}

    @PutMapping("/update-by-fileid")
    public ResponseEntity<Survey> updateSurveyByFileDataId(
            @RequestParam String surveyName,
            @RequestParam String fileDataId,
            @RequestBody Survey updatedSurvey) {

        // Find the Survey by surveyName and fileDataId
        Optional<Survey> existingSurveyOptional = surveyRepository.findBySurveyNameAndFileDataId(surveyName, fileDataId);

        if (!existingSurveyOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // If the Survey doesn't exist, return 404
        }

        Survey existingSurvey = existingSurveyOptional.get();

        // Update fields of the existing Survey entity with the new values from the request body
        existingSurvey.setPhoneNumber(updatedSurvey.getPhoneNumber());
        existingSurvey.setVoter_type(updatedSurvey.getVoter_type());
        existingSurvey.setBooth(updatedSurvey.getBooth());
        existingSurvey.setConstituency(updatedSurvey.getConstituency());
        existingSurvey.setHouseNumber(updatedSurvey.getHouseNumber());
        existingSurvey.setGender(updatedSurvey.getGender());
        existingSurvey.setName(updatedSurvey.getName());
        existingSurvey.setVoterId(updatedSurvey.getVoterId());
        existingSurvey.setVoterStatus(updatedSurvey.getVoterStatus());
        existingSurvey.setWhatsappNumber(updatedSurvey.getWhatsappNumber());
        existingSurvey.setQues1(updatedSurvey.getQues1());
        existingSurvey.setQues2(updatedSurvey.getQues2());
        existingSurvey.setQues3(updatedSurvey.getQues3());
        existingSurvey.setQues4(updatedSurvey.getQues4());
        existingSurvey.setQues5(updatedSurvey.getQues5());
        existingSurvey.setQues6(updatedSurvey.getQues6());
        existingSurvey.setRole(updatedSurvey.getRole());
        existingSurvey.setAge(updatedSurvey.getAge());
        existingSurvey.setSurveyName(updatedSurvey.getSurveyName());
        existingSurvey.setReligion(updatedSurvey.getReligion());
        existingSurvey.setUserId(updatedSurvey.getUserId());
        existingSurvey.setUpdatedBy(updatedSurvey.getUpdatedBy());
        existingSurvey.setUpdatedDate(updatedSurvey.getUpdatedDate()); // Update the timestamp for updatedDate

        // Save the updated Survey back to the database
        surveyRepository.save(existingSurvey);

        // Return the updated Survey
        return ResponseEntity.ok(existingSurvey);
    }

    @PutMapping("/update-by-id")
    public ResponseEntity<Survey> updateSurveyById(
            @RequestParam String surveyName,
            @RequestParam Long id,
            @RequestBody Survey updatedSurvey) {

        // Find the Survey by surveyName and id
        Optional<Survey> existingSurveyOptional = surveyRepository.findBySurveyNameAndId(surveyName, id);

        if (!existingSurveyOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // If the Survey doesn't exist, return 404
        }

        Survey existingSurvey = existingSurveyOptional.get();

        // Update fields of the existing Survey entity with the new values from the request body
        existingSurvey.setPhoneNumber(updatedSurvey.getPhoneNumber());
        existingSurvey.setVoter_type(updatedSurvey.getVoter_type());
        existingSurvey.setBooth(updatedSurvey.getBooth());
        existingSurvey.setConstituency(updatedSurvey.getConstituency());
        existingSurvey.setHouseNumber(updatedSurvey.getHouseNumber());
        existingSurvey.setGender(updatedSurvey.getGender());
        existingSurvey.setName(updatedSurvey.getName());
        existingSurvey.setVoterId(updatedSurvey.getVoterId());
        existingSurvey.setVoterStatus(updatedSurvey.getVoterStatus());
        existingSurvey.setWhatsappNumber(updatedSurvey.getWhatsappNumber());
        existingSurvey.setQues1(updatedSurvey.getQues1());
        existingSurvey.setQues2(updatedSurvey.getQues2());
        existingSurvey.setQues3(updatedSurvey.getQues3());
        existingSurvey.setQues4(updatedSurvey.getQues4());
        existingSurvey.setQues5(updatedSurvey.getQues5());
        existingSurvey.setQues6(updatedSurvey.getQues6());
        existingSurvey.setReligion(updatedSurvey.getReligion());
        existingSurvey.setUpdatedBy(updatedSurvey.getUpdatedBy());
        existingSurvey.setRole(updatedSurvey.getRole());
        existingSurvey.setAge(updatedSurvey.getAge());
        existingSurvey.setSurveyName(updatedSurvey.getSurveyName());
        existingSurvey.setUserId(updatedSurvey.getUserId());
        existingSurvey.setUpdatedDate(updatedSurvey.getUpdatedDate()); // Update the timestamp for updatedDate

        // Save the updated Survey back to the database
        surveyRepository.save(existingSurvey);

        // Return the updated Survey
        return ResponseEntity.ok(existingSurvey);
    }

    @GetMapping("/survey-by-fileid")
    public ResponseEntity<Survey> getSurveyByFileDataId(@RequestParam String fileDataId) {
        Optional<Survey> surveyOptional = surveyRepository.findByFileDataId(fileDataId);

        if (!surveyOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }

        return ResponseEntity.ok(surveyOptional.get()); // Return the survey if found
    }

    @GetMapping("/survey-by-id")
    public ResponseEntity<Survey> getSurveyById(@RequestParam Long id) {
        Optional<Survey> surveyOptional = surveyRepository.findById(id);

        if (!surveyOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }

        return ResponseEntity.ok(surveyOptional.get()); // Return the survey if found
    }

}
