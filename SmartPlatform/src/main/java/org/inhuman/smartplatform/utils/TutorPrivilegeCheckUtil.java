package org.inhuman.smartplatform.utils;


public class TutorPrivilegeCheckUtil {

    public static void checkHomeworkPrivilege(int id, int lessonId, int type) throws Exception {
        if(!DatabaseUtil.getTutorPrivilege(id, lessonId, type)){
            throw new Exception("no privilege");
        }
    }
}
