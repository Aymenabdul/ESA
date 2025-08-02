package com.survey.esa.survey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/filterByConstituencyAndBooth")
    public ResponseEntity<Map<String, Map<String, Long>>> getSurveyVoteCountsByFilter(
            @RequestParam("constituency") String constituency,
            @RequestParam(value = "booth", required = false) String booth) {

        // Trim spaces and make the comparison case-insensitive for both constituency
        // and booth
        constituency = constituency.trim().toLowerCase();
        if (booth != null && !booth.isEmpty()) {
            booth = booth.trim().toLowerCase(); // Trim any extra spaces from booth number
        }

        Map<String, Map<String, Long>> voteCounts;

        try {
            // Call the service method to get the counts based on provided parameters
            if (booth == null || booth.isEmpty()) {
                // If booth is not provided, filter by constituency only
                voteCounts = surveyService.getSurveyVoteCountsFiltered(constituency, null);
            } else {
                // If booth is provided, filter by both constituency and booth
                voteCounts = surveyService.getSurveyVoteCountsFiltered(constituency, booth);
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


}