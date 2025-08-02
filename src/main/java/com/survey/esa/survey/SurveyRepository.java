package com.survey.esa.survey;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    // For ques1
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


   // For filtering by constituency and booth (ignoring spaces in booth)
@Query(value = "SELECT s.ques1, COUNT(*) FROM survey s WHERE LOWER(REPLACE(s.constituency, ' ', '')) = LOWER(:constituency) AND LOWER(REPLACE(s.booth, ' ', '')) = LOWER(:booth) GROUP BY s.ques1", nativeQuery = true)
List<Object[]> countVotesByQues1Filtered(@Param("constituency") String constituency, @Param("booth") String booth);

// Repeat for other questions (ques2, ques3, etc.)
@Query(value = "SELECT s.ques2, COUNT(*) FROM survey s WHERE LOWER(REPLACE(s.constituency, ' ', '')) = LOWER(:constituency) AND LOWER(REPLACE(s.booth, ' ', '')) = LOWER(:booth) GROUP BY s.ques2", nativeQuery = true)
List<Object[]> countVotesByQues2Filtered(@Param("constituency") String constituency, @Param("booth") String booth);

@Query(value = "SELECT s.ques3, COUNT(*) FROM survey s WHERE LOWER(REPLACE(s.constituency, ' ', '')) = LOWER(:constituency) AND LOWER(REPLACE(s.booth, ' ', '')) = LOWER(:booth) GROUP BY s.ques3", nativeQuery = true)
List<Object[]> countVotesByQues3Filtered(@Param("constituency") String constituency, @Param("booth") String booth);

@Query(value = "SELECT s.ques4, COUNT(*) FROM survey s WHERE LOWER(REPLACE(s.constituency, ' ', '')) = LOWER(:constituency) AND LOWER(REPLACE(s.booth, ' ', '')) = LOWER(:booth) GROUP BY s.ques4", nativeQuery = true)
List<Object[]> countVotesByQues4Filtered(@Param("constituency") String constituency, @Param("booth") String booth);

@Query(value = "SELECT s.ques5, COUNT(*) FROM survey s WHERE LOWER(REPLACE(s.constituency, ' ', '')) = LOWER(:constituency) AND LOWER(REPLACE(s.booth, ' ', '')) = LOWER(:booth) GROUP BY s.ques5", nativeQuery = true)
List<Object[]> countVotesByQues5Filtered(@Param("constituency") String constituency, @Param("booth") String booth);

@Query(value = "SELECT s.ques6, COUNT(*) FROM survey s WHERE LOWER(REPLACE(s.constituency, ' ', '')) = LOWER(:constituency) AND LOWER(REPLACE(s.booth, ' ', '')) = LOWER(:booth) GROUP BY s.ques6", nativeQuery = true)
List<Object[]> countVotesByQues6Filtered(@Param("constituency") String constituency, @Param("booth") String booth);


@Query(value = "SELECT s.ques2, COUNT(*) FROM survey s WHERE s.constituency = :constituency GROUP BY s.ques2", nativeQuery = true)
List<Object[]> countVotesByQues2FilteredByConstituency(@Param("constituency") String constituency);

@Query(value = "SELECT s.ques3, COUNT(*) FROM survey s WHERE s.constituency = :constituency GROUP BY s.ques3", nativeQuery = true)
List<Object[]> countVotesByQues3FilteredByConstituency(@Param("constituency") String constituency);

@Query(value = "SELECT s.ques4, COUNT(*) FROM survey s WHERE s.constituency = :constituency GROUP BY s.ques4", nativeQuery = true)
List<Object[]> countVotesByQues4FilteredByConstituency(@Param("constituency") String constituency);

@Query(value = "SELECT s.ques5, COUNT(*) FROM survey s WHERE s.constituency = :constituency GROUP BY s.ques5", nativeQuery = true)
List<Object[]> countVotesByQues5FilteredByConstituency(@Param("constituency") String constituency);

@Query(value = "SELECT s.ques6, COUNT(*) FROM survey s WHERE s.constituency = :constituency GROUP BY s.ques6", nativeQuery = true)
List<Object[]> countVotesByQues6FilteredByConstituency(@Param("constituency") String constituency);


// For filtering by constituency only
@Query(value = "SELECT s.ques1, COUNT(*) FROM survey s WHERE LOWER(s.constituency) = LOWER(:constituency) GROUP BY s.ques1", nativeQuery = true)
List<Object[]> countVotesByQues1FilteredByConstituency(@Param("constituency") String constituency);

// For filtering by constituency and booth

// Repeat the above for other questions (ques2, ques3, etc.)
List<Survey> findAll();

}
