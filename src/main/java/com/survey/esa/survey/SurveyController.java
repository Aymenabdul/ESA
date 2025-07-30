package com.survey.esa.survey;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    // Endpoint to submit a survey response
    @PostMapping("/submit")
    public Survey submitSurvey(@RequestBody Survey survey) {
        return surveyService.saveSurvey(survey);
    }

    @GetMapping("/getAllSurveys")
    public List<Survey> getAllSurveys() {
        return surveyService.getAllSurveys();  // Call the method in the service
    }

    @GetMapping("/voteCounts")
    public Map<String, Map<String, Long>> getSurveyVoteCounts() {
        return surveyService.getSurveyVoteCounts();  // Returns a Map for each question
    }

    @GetMapping("/filterByConstituencyAndBooth")
    public Map<String, Map<String, Long>> getSurveyVoteCountsByFilter(
            @RequestParam("constituency") String constituency,
            @RequestParam("booth") String booth) {

        return surveyService.getSurveyVoteCountsFiltered(constituency, booth);
    }
}