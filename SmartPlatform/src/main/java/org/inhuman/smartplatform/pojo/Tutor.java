package org.inhuman.smartplatform.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tutor {
    private int tutor_id;
    private String username;
    private int lesson_id;
    private String lessonName;
    private Boolean homework_control;
    private Boolean docs_control;
    private Boolean points_control;
    private Boolean exam_control;
}
