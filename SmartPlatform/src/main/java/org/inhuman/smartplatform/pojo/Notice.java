package org.inhuman.smartplatform.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    private int id;
    private String title;
    private String content;
    // 0 系统统一发送
    private String sender;
    // 0 系统统一接收
    private String receiver;
    private LocalDateTime time;
    // 0 未读，1 已读
    private int state;
}
