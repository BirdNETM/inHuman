package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.inhuman.smartplatform.pojo.Homework;

import java.util.List;

@Mapper
public interface HomeworkMapper {

    @Select("select * from homework where homework.lessonId = #{lessonId}")
    List<Homework> getHomeworks(int id, int lessonId);

    @Select("select homework.filesUrl from homework where id = #{homeworkId}")
    String getDocsUrlById(int id, int homeworkId);

    @Select("select homework.* from homework where id = #{homeworkId}")
    Homework getHomeworksById(int id, int homeworkId);
}
