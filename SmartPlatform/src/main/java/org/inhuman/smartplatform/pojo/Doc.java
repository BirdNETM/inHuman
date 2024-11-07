package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doc {
    private int id;
    private String docName;
    private String docType;
    private int docFatherId;
    private String docUrl;
}
