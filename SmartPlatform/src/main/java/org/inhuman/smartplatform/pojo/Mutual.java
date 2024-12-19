package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mutual {
    int homeWorkId;
    int reviewerId;
    int revieweeId;
    String state;
    LocalDateTime submitTime;
    LocalDateTime deadline;
}
