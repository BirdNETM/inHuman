package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question
{
    int id;
    String type;
    double mark;
    String description;
    String suggestedAnswer;
    List<String> choices;
}
