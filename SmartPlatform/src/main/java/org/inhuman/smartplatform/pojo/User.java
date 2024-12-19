package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    private String userName;
    private String password;
    // 0 学生，1 老师，2 助教
    private int position;

    public User(int mockUserId, String testUser) {
    }
}
