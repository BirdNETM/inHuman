package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int senderId;
    private int postingId;
    private String username;
    private String Content;
    private Date Time;
}
