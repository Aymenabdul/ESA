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
    // Log the received survey object to check its contents
    System.out.println("Saving Survey: " + survey);
    return surveyRepository.save(survey);
}
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll(); // This fetches all Survey entities from the DB
    }

   public Map<String, Map<String, Long>> getSurveyVoteCountsFiltered(String surveyName, String constituency, String booth) {
    Map<String, Map<String, Long>> voteCounts = new HashMap<>();

    // Query based on provided filters
    if (constituency == null && booth == null) {
        // Filter by surveyName only
        voteCounts.put("ques1", getVoteCount(surveyRepository.countVotesByQues1FilteredBySurveyName(surveyName)));
        voteCounts.put("ques2", getVoteCount(surveyRepository.countVotesByQues2FilteredBySurveyName(surveyName)));
        voteCounts.put("ques3", getVoteCount(surveyRepository.countVotesByQues3FilteredBySurveyName(surveyName)));
        voteCounts.put("ques4", getVoteCount(surveyRepository.countVotesByQues4FilteredBySurveyName(surveyName)));
        voteCounts.put("ques5", getVoteCount(surveyRepository.countVotesByQues5FilteredBySurveyName(surveyName)));
        voteCounts.put("ques6", getVoteCount(surveyRepository.countVotesByQues6FilteredBySurveyName(surveyName)));
    } else if (booth == null || booth.isEmpty()) {
        // Filter by surveyName and constituency only
        voteCounts.put("ques1", getVoteCount(surveyRepository.countVotesByQues1FilteredByConstituency(surveyName, constituency)));
        voteCounts.put("ques2", getVoteCount(surveyRepository.countVotesByQues2FilteredByConstituency(surveyName, constituency)));
        voteCounts.put("ques3", getVoteCount(surveyRepository.countVotesByQues3FilteredByConstituency(surveyName, constituency)));
        voteCounts.put("ques4", getVoteCount(surveyRepository.countVotesByQues4FilteredByConstituency(surveyName, constituency)));
        voteCounts.put("ques5", getVoteCount(surveyRepository.countVotesByQues5FilteredByConstituency(surveyName, constituency)));
        voteCounts.put("ques6", getVoteCount(surveyRepository.countVotesByQues6FilteredByConstituency(surveyName, constituency)));
    } else {
        // Filter by surveyName, constituency, and booth
        voteCounts.put("ques1", getVoteCount(surveyRepository.countVotesByQues1Filtered(surveyName, constituency, booth)));
        voteCounts.put("ques2", getVoteCount(surveyRepository.countVotesByQues2Filtered(surveyName, constituency, booth)));
        voteCounts.put("ques3", getVoteCount(surveyRepository.countVotesByQues3Filtered(surveyName, constituency, booth)));
        voteCounts.put("ques4", getVoteCount(surveyRepository.countVotesByQues4Filtered(surveyName, constituency, booth)));
        voteCounts.put("ques5", getVoteCount(surveyRepository.countVotesByQues5Filtered(surveyName, constituency, booth)));
        voteCounts.put("ques6", getVoteCount(surveyRepository.countVotesByQues6Filtered(surveyName, constituency, booth)));
    }

    return voteCounts;
}

private Map<String, Long> getVoteCount(List<Object[]> results) {
    Map<String, Long> voteCounts = new HashMap<>();
    for (Object[] result : results) {
        String option = (String) result[0];  // Option like "Yes", "No", etc.
        Long count = (Long) result[1];  // Count of votes
        voteCounts.put(option, count);
    }
    return voteCounts;
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
            String option = (String) result[0]; // Option name (Bad, Average, etc.)
            Long count = (Long) result[1]; // Vote count
            voteCounts.put(option, count);
        }
        return voteCounts;
    }
}
