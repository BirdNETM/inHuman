package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.Answer;
import org.inhuman.smartplatform.pojo.ExamSubmit;
import org.inhuman.smartplatform.pojo.Question;
import org.inhuman.smartplatform.pojo.Summary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PaperService {

   void assignQuestion(int id,int examId, String type, String description, String suggestedAnswer, Double mark, List<String> choices) throws Exception;

   void receiveHomework(int id,int studentId, int examId, Map<Integer, String> answers);

   void manualScore(int id,int studentId, int examId, Map<Integer, Double> marks) throws Exception;

   Summary summary(int id,int examId);

   List<Question> getQuestions(int id,int examId);

   List<Answer> getExamSubmits(int id,int examId, int studentId);

   Question getQuestionById(int id,int questionId);

   double getMarkById(int id, int examId);

   List<Answer> getAnswersByQuestionId(int id,int QuestionId);
}
