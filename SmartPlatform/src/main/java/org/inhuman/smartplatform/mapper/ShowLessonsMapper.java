package org.inhuman.smartplatform.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.inhuman.smartplatform.pojo.Doc;
import org.inhuman.smartplatform.pojo.Lesson;

import java.util.List;

@Mapper
public interface ShowLessonsMapper {
    @Select("select lesson.id,lesson.name,lesson.teacher from lesson join teaching where teaching.user_id = #{user_id} and teaching.lesson_id = lesson.id")
    List<Lesson> findLessonsByUserId(int user_id);

    @Select("SELECT lesson.id, lesson.name, lesson.teacher " +
            "FROM lesson JOIN teaching " +
            "ON teaching.lesson_id = lesson.id " +
            "WHERE teaching.user_id = #{userId} " +
            "AND lesson.name LIKE CONCAT('%', #{searchKey}, '%')")
    List<Lesson> findLessonsByUserIdAndSearchKey(int userId, String searchKey);

    @Select("select lesson.* from lesson join teaching t on lesson.id = t.lesson_id WHERE t.user_id = #{id} and lesson.id = #{lessonId}")
    Lesson showLessonDetail(int id, int lessonId);


    @Select("select docs.* from docs join lesson on docs.lessonId = lesson.id join teaching on lesson.id = teaching.lesson_id where teaching.user_id = #{id} and docs.lessonId = #{lessonId} and docs.docFatherId = #{fatherId}")
    List<Doc> getLessonsDocs(int id, int lessonId, int fatherId);


    @Select("select docs.docUrl from docs join lesson on docs.lessonId = lesson.id join teaching on lesson.id = teaching.lesson_id where docs.id = #{docId} and teaching.user_id = #{id}")
    //@Select("select docs.docUrl from docs where docs.id = #{docId}")
    String getDocsUrlById(int id, int docId);

    @Update("update lesson set outline = #{outline} where id = #{lessonId}")
    void updateLessonOutline(int id, int lessonId, String outline);

    @Delete("delete from docs where id = #{docsId}")
    void deleteLessonDocs(int id, int docsId);

    @Delete("delete from docs where docFatherId = #{docsId}")
    void deleteLessonSubDocs(int id, int docsId);

    @Update("update docs set downloadLicense = #{license} where id = #{docsId}")
    void updateDownloadLicense(int id, int docsId, int license);
}
