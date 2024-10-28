package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Homework {
    private int id;
    private String title;
    private String content;
    private String filesUrl;
    private Date publicTime;
    private Date overTime;
    private boolean completed;
}
