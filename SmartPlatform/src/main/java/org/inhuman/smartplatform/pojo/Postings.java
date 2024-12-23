package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postings {
    private int id;
    private String username;
    private int posterId;
    private int lessonId;
    private String title;
    private String content;
    private Date time;
    private String type;
    private int pictureCounts;
}
