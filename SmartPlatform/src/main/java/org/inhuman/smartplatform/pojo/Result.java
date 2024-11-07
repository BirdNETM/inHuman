package org.inhuman.smartplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private boolean success;
    private String message;
    private Object data;

    public static Result success(){
        return new Result(true,"success",null);
    }

    public static Result success(Object data){
        return new Result(true,"success",data);
    }

    public static Result error(String message){
        return new Result(false,message,null);
    }
}
