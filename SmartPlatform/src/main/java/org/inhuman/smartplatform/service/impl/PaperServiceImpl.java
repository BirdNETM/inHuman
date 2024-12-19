package org.inhuman.smartplatform.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.PaperMapper;
import org.inhuman.smartplatform.pojo.*;
import org.inhuman.smartplatform.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class PaperServiceImpl implements PaperService {
    @Autowired
    PaperMapper paperMapper;

    @Override
    public void assignQuestion(int id, int examId, String type, String description, String suggestedAnswer, Double mark, List<String> choices){
        Paper paper = new Paper();
        paper.setExamId(examId);
        paper.setType(type);
        paper.setMark(mark);
        paper.setDescription(description);
        paper.setSuggestedAnswer(suggestedAnswer);
        paperMapper.assignQuestion(paper);
        for(String choice : choices){
            paperMapper.assignChoiceQuestion(paper.getId(), choice);
        }
    }

    @Override
    public void receiveHomework(int id, int studentId, int examId, Map<Integer, String> answers){
        ExamSubmit examSubmit = new ExamSubmit();
        Map<String, Integer> map = new HashMap<>();
        examSubmit.setStudentId(studentId);
        examSubmit.setExamId(examId);

        double choiceMarks = 0;
        if(paperMapper.get1(examId, studentId) == 0){
            paperMapper.examSubmit(examSubmit);
            for(int key : answers.keySet()){
                paperMapper.answerSubmit(examSubmit.getId(), key, answers.get(key));
                if(Objects.equals(paperMapper.getTypeById(key), "c") && Objects.equals(paperMapper.getAnswerById(key), answers.get(key))){
                    double mark = paperMapper.getMarkById(key);
                    choiceMarks += mark;
                    paperMapper.recordMark(examSubmit.getId(),key,mark);
                }
            }
        }
        else{
            examSubmit.setId(paperMapper.getid(studentId, examId));
            for(int key : answers.keySet()){
                if(paperMapper.get2(examSubmit.getId(), key) == 0) {
                    paperMapper.answerSubmit(examSubmit.getId(), key, answers.get(key));
                }
                else {
                    paperMapper.updateAnswer(examSubmit.getId(), key, answers.get(key));
                }
                if(Objects.equals(paperMapper.getTypeById(key), "c") && Objects.equals(paperMapper.getAnswerById(key), answers.get(key))){
                    double mark = paperMapper.getMarkById(key);
                    choiceMarks += mark;
                    paperMapper.recordMark(examSubmit.getId(),key,mark);
                }
                else{
                    paperMapper.recordMark(examSubmit.getId(),key,0);
                }
            }
        }
        paperMapper.recordTotalChoice(examSubmit.getId(),choiceMarks);
    }

    @Override
    public void manualScore(int id, int studentId, int examId, Map<Integer, Double> marks){
        int id_ = paperMapper.getPaperId(examId, studentId);
        for(int key : marks.keySet()){
            System.out.println(key);
            System.out.println(marks.get(key));
            paperMapper.recordMark(id_, key, marks.get(key));
        }
    }

    @Override
    public Summary summary(int id,int examId){
        Summary summary = new Summary();
        summary.setTotal(paperMapper.getTotalMark(examId));
        summary.setAverage(paperMapper.getAverageMark(examId));
        return summary;
    }

    @Override
    public List<Question> getQuestions(int id, int examId) {
        List<Question> questions = paperMapper.getQuestionList(examId);
        for(Question question : questions){
            if(Objects.equals(question.getType(), "c")){
                List<String> choices = paperMapper.getChoiceList(question.getId());
                question.setChoices(choices);
            }
        }
        return(questions);
    }

    @Override
    public List<Answer> getExamSubmits(int id, int examId, int studentId) {
        int id_ = paperMapper.getPaperId(examId, studentId);
        return(paperMapper.getAnswerList(id_));
    }

    @Override
    public Question getQuestionById(int id, int questionId) {
        System.out.println("------------------------------------------------");
        System.out.println(questionId);
        Question question = paperMapper.getQuestionById(questionId);
        if(Objects.equals(question.getType(), "c")){
            List<String> choices = paperMapper.getChoiceList(questionId);
            question.setChoices(choices);
        }
        return (question);
    }

    @Override
    public double getMarkById(int id, int examId) {
        System.out.println("------------------------------------");
        System.out.println(id);
        System.out.println(examId);

        return (paperMapper.gettotalMarkById(paperMapper.getid(id, examId)));
    }

    @Override
    public List<Answer> getAnswersByQuestionId(int id, int QuestionId) {
        List<Answer> answers = paperMapper.getAnswerByQuestion(QuestionId);
        for(Answer answer : answers){
            answer.setStudentId(paperMapper.getStudentId(answer.getId()));
        }
        return answers;
    }

    @Override
    public void deleteQuestion(int id, int questionId) {
        paperMapper.deleteQuestion(questionId);
    }

    @Override
    public void updateQuestion(int id, int questionId, int examId, String type, String description, String suggestedAnswer, double mark, List<String> choices) {
        System.out.println(description);
        paperMapper.updateQuestion(questionId, examId, type, description, suggestedAnswer, mark, choices);
    }

    @Override
    public void deleteChoice(int id, int questionId, String choice) {
        paperMapper.deletechoice(id, choice);
    }

    @Override
    public void updateChoice(int id, int questionId, String oldChoice, String newChoice) {
        paperMapper.updateChoice(id, oldChoice, newChoice);
    }
}
