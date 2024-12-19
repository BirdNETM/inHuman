package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.*;
import org.inhuman.smartplatform.pojo.Answer;
import org.inhuman.smartplatform.pojo.ExamSubmit;
import org.inhuman.smartplatform.pojo.Paper;
import org.inhuman.smartplatform.pojo.Question;

import java.sql.Time;
import java.util.List;

@Mapper
public interface PaperMapper {

    @Insert("insert into questions(examId, type, mark, description, suggestedAnswer) " +
            "values (#{examId}, #{type}, #{mark}, #{description}, #{suggestedAnswer})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void assignQuestion(Paper paper);

    @Insert("insert into choicequestions " +
            "values(#{id}, #{choice})")
    void assignChoiceQuestion(int id, String choice);

    @Insert("insert into examsubmit(examId, studentId) values " +
            "(#{examId}, #{studentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void examSubmit(ExamSubmit examSubmit);

    @Insert("insert into answers(id, questionId, answer) values " +
            "(#{id}, #{questionId}, #{answer})")
    void answerSubmit(int id, int questionId, String answer);

    @Select("select suggestedAnswer from questions where id = #{id}")
    String getAnswerById(int id);

    @Select("select mark from questions where id = #{id}")
    double getMarkById(int id);

    @Select("select type from questions where id = #{id}")
    String getTypeById(int id);

    @Select("select id from examsubmit where examId = #{examId} and studentId = #{studentId}")
    int getPaperId(int examId, int studentId);

    @Update("update answers set mark = #{mark} where id = #{id} and questionId = #{questionId}")
    void recordMark(int id, int questionId, double mark);

    @Update("update examsubmit set totalChoice = #{totalChoice} where id = #{id}")
    void recordTotalChoice(int id, double totalChoice);

    @Select("select SUM(totalMark) from examsubmit where examId = #{examId}")
    double getTotalMark(int examId);

    @Select("select Avg(totalMark) from examsubmit where examId = #{examId}")
    double getAverageMark(int examId);

    @Select("select * from examsubmit where id = #{id}")
    List<ExamSubmit> getExamSubmitList(int id);

    @Select("select answers.*, questions.description from answers join questions where answers.id = #{id} and answers.questionId = questions.id")
    List<Answer> getAnswerList(int id);

    @Select("select * from questions where examId = #{examId}")
    List<Question> getQuestionList(int examId);

    @Select("select * from questions where id = #{id}")
    Question getQuestionById(int id);

    @Select("SELECT COALESCE(totalMark, 0) FROM examsubmit WHERE studentId = #{id} AND examId = #{examId}")
    double getTotalMarkById(int id, int examId);

    @Select("select SUM(mark) from answers where id = #{id} ")
    double gettotalMarkById(int id);

    @Select("select choice from choicequestions where id = #{id} ")
    List<String> getChoiceList(int id);

    @Select("SELECT COUNT(*) FROM examsubmit WHERE examId = #{examId} AND studentId = #{studentId}")
    int get1(@Param("examId") int examId, @Param("studentId") int studentId);

    @Select("select id from examsubmit where studentId = #{studentId} and examId = #{examId}")
    int getid(int studentId, int examId);

    @Update("UPDATE answers SET answer = #{answer} WHERE id = #{id} AND questionId = #{questionId}")
    void updateAnswer(@Param("id") int id, @Param("questionId") int questionId, @Param("answer") String answer);

    @Select("SELECT COUNT(*) FROM answers WHERE id = #{id} AND questionId = #{questionId}")
    int get2(@Param("id") int id, @Param("questionId") int questionId);

    @Select("select * from answers where questionId = #{questionId}")
    List<Answer> getAnswerByQuestion(int questionId);

    @Select("select studentId from examsubmit where id = #{id}")
    int getStudentId(int id);

    @Delete("delete from questions where id = #{questionId}")
    void deleteQuestion(int questionId);

    @Update({
            "UPDATE questions SET examId = #{examId}, " +
                    "type = #{type}," +
                    "description = #{description}, " +
                    "suggestedAnswer = #{suggestedAnswer}," +
                    "mark = #{mark} " +
            "WHERE id = #{questionId}"
    })
    void updateQuestion(@Param("questionId") int questionId,
                        @Param("examId") int examId,
                        @Param("type") String type,
                        @Param("description") String description,
                        @Param("suggestedAnswer") String suggestedAnswer,
                        @Param("mark") double mark,
                        @Param("choices") List<String> choices);

    @Delete("delete from choicequestions where id = #{id} and choice = #{choice}")
    void deletechoice(int id, String choice);

    @Update("update choicequestions set choice = #{newChoice} where id = #{id} and choice = #{choice}")
    void updateChoice(int id, String oldChoice, String newChoice);
}
