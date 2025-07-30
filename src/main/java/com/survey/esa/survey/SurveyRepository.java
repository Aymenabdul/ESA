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

    @Query("SELECT s.ques1, COUNT(s) FROM Survey s WHERE s.constituency = :constituency AND s.booth = :booth GROUP BY s.ques1")
    List<Object[]> countVotesByQues1Filtered(String constituency, String booth);

    // For ques2 with filter (constituency and booth number)
    @Query("SELECT s.ques2, COUNT(s) FROM Survey s WHERE s.constituency = :constituency AND s.booth = :booth GROUP BY s.ques2")
    List<Object[]> countVotesByQues2Filtered(String constituency, String booth);

    // For ques3 with filter (constituency and booth number)
    @Query("SELECT s.ques3, COUNT(s) FROM Survey s WHERE s.constituency = :constituency AND s.booth = :booth GROUP BY s.ques3")
    List<Object[]> countVotesByQues3Filtered(String constituency, String booth);

    // For ques4 with filter (Performance of previous CM - Palaniswami)
    @Query("SELECT s.ques4, COUNT(s) FROM Survey s WHERE s.constituency = :constituency AND s.booth = :booth GROUP BY s.ques4")
    List<Object[]> countVotesByQues4Filtered(String constituency, String booth);

    // For performance of the current CM - Stalin
    @Query("SELECT s.ques5, COUNT(s) FROM Survey s WHERE s.constituency = :constituency AND s.booth = :booth GROUP BY s.ques5")
    List<Object[]> countVotesByQues5Filtered(String constituency, String booth);

    // For performance of the current MLA
    @Query("SELECT s.ques6, COUNT(s) FROM Survey s WHERE s.constituency = :constituency AND s.booth = :booth GROUP BY s.ques6")
    List<Object[]> countVotesByQues6Filtered(String constituency, String booth);
}