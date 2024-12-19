package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paper {
    int id;
    int examId;
    String type;
    Double mark;
    String description;
    String suggestedAnswer;
}
