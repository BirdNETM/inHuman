package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.inhuman.smartplatform.pojo.Mutual;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MutualEvaluationMapper {
    @Insert("INSERT INTO mutualtasks VALUES (#{homeworkId}, #{deadLine})")
    void assignTasks(int homeworkId, LocalDateTime deadLine );

    @Insert("""
    INSERT INTO specificMutual (reviewerId, revieweeId, homeWorkId, submitTime)
    WITH Randomized AS (
        SELECT
            user_id,
            ROW_NUMBER() OVER (ORDER BY RAND()) AS rand_order
        FROM teaching
        WHERE lesson_id = #{lessonId}
    ),
    Shifted AS (
        SELECT
            r1.user_id AS reviewer_id,
            COALESCE(LEAD(r1.user_id) OVER (ORDER BY r1.rand_order), 
                     (SELECT user_id FROM Randomized WHERE rand_order = 1)) AS reviewee_id
        FROM Randomized r1
    )
    SELECT reviewer_id, reviewee_id, #{homeWorkId} AS homeWorkId, NOW() AS submitTime FROM Shifted;
""")
    void generatePeerReviewTasks(@Param("lessonId") int lessonId, @Param("homeWorkId") int homeWorkId);




    @Update("""
    UPDATE specificMutual 
    SET STATE = 'Y', score = #{score}
    WHERE homeWorkId = #{homeWorkId}
      AND reviewerId = #{reviewerId} 
      AND revieweeId = #{revieweeId}
""")
    void receiveReview(@Param("homeWorkId") int homeWorkId,
                       @Param("reviewerId") int reviewerId,
                       @Param("revieweeId") int revieweeId,
                       @Param("score") int score);

    @Select("SELECT deadLine FROM mutualtasks WHERE homeWorkId = #{homeWorkId} LIMIT 1")
    LocalDateTime findDeadLine(@Param("homeWorkId") int homeWorkId);

    @Select("SELECT lessonId from homework where id = #{id}")
    int getLessonIdById(@Param("id") int id);

    @Select("SELECT specificmutual.*, mutualtasks.deadLine " +
            "from specificmutual " +
            "join mutualtasks " +
            "where mutualtasks.homeWorkId = #{homeWorkId} " +
            "and specificmutual.reviewerId = #{userId} " +
            "and specificmutual.homeWorkId = mutualtasks.homeWorkId")
    List<Mutual> getSpecificMutualTasks(int userId, int homeWorkId);

    @Select("select fileUrl from homework_submit " +
            "where homeworkId = #{homeworkId} " +
            "and studentId = #{id}")
    String getDocsUrlById(int id, int homeworkId);
}
