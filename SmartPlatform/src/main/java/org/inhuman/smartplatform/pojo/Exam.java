package org.inhuman.smartplatform.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    private int id;
    private String examName;
    private int lessonId;
    private String lessonName;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examStartTime;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examEndTime;
    private String examDescription;
    private List<Classroom> classrooms;

    public Exam(int i, String physicsExam, String s, int i1) {
    }
}
