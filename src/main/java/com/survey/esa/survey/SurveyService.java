package com.survey.esa.survey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    // Method to save survey response
    public Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

     public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();  // This fetches all Survey entities from the DB
    }

     public Map<String, Map<String, Long>> getSurveyVoteCounts() {
        Map<String, Map<String, Long>> allVoteCounts = new HashMap<>();

        // Add counts in the order: ques1, ques2, ques3, ques4, ques5, ques6

        // Get counts for ques1
        Map<String, Long> ques1VoteCounts = getVoteCounts(surveyRepository.countVotesByQues1());
        allVoteCounts.put("ques1", ques1VoteCounts);

        // Get counts for ques2
        Map<String, Long> ques2VoteCounts = getVoteCounts(surveyRepository.countVotesByQues2());
        allVoteCounts.put("ques2", ques2VoteCounts);

        // Get counts for ques3
        Map<String, Long> ques3VoteCounts = getVoteCounts(surveyRepository.countVotesByQues3());
        allVoteCounts.put("ques3", ques3VoteCounts);

        // Get counts for ques4 (Performance of previous CM - Palaniswami)
        Map<String, Long> ques4VoteCounts = getVoteCounts(surveyRepository.countVotesByQues4());
        allVoteCounts.put("ques4", ques4VoteCounts);

        // Get counts for ques5 (Performance of current CM - Stalin)
        Map<String, Long> ques5VoteCounts = getVoteCounts(surveyRepository.countVotesByQues5());
        allVoteCounts.put("ques5", ques5VoteCounts);

        // Get counts for ques6 (Performance of current MLA)
        Map<String, Long> ques6VoteCounts = getVoteCounts(surveyRepository.countVotesByQues6());
        allVoteCounts.put("ques6", ques6VoteCounts);

        return allVoteCounts;
    }

    // Helper method to convert query result into a map
    private Map<String, Long> getVoteCounts(List<Object[]> results) {
        Map<String, Long> voteCounts = new HashMap<>();
        for (Object[] result : results) {
            String option = (String) result[0];  // Option name (Bad, Average, etc.)
            Long count = (Long) result[1];       // Vote count
            voteCounts.put(option, count);
        }
        return voteCounts;
    }
}
