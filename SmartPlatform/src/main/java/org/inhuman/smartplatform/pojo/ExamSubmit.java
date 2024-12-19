package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmit {
    public int id;
    public int studentId;
    public int examId;
    public double totalChoice;
    public double totalMark;
}
