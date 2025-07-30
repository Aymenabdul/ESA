package com.survey.esa.survey;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    @Query("SELECT s.ques1, COUNT(s) FROM Survey s GROUP BY s.ques1")
    List<Object[]> countVotesByQues1();

    // For ques2
    @Query("SELECT s.ques2, COUNT(s) FROM Survey s GROUP BY s.ques2")
    List<Object[]> countVotesByQues2();

    // For ques3
    @Query("SELECT s.ques3, COUNT(s) FROM Survey s GROUP BY s.ques3")
    List<Object[]> countVotesByQues3();

    @Query("SELECT s.ques4, COUNT(s) FROM Survey s GROUP BY s.ques4")
    List<Object[]> countVotesByQues4();

    // For performance of the current Chief Minister (Stalin)
    @Query("SELECT s.ques5, COUNT(s) FROM Survey s GROUP BY s.ques5")
    List<Object[]> countVotesByQues5();

    // For performance of the current MLA
    @Query("SELECT s.ques6, COUNT(s) FROM Survey s GROUP BY s.ques6")
    List<Object[]> countVotesByQues6();
}
