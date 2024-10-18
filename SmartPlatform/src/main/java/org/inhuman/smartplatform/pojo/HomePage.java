package org.inhuman.smartplatform.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePage {
    private int id;
    private String userName;
    private String bio;
    private String gender;
    private String avatarUrl;
    private Boolean privacy;
    private String favorites;
}
