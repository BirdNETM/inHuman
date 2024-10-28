package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.Doc;
import org.inhuman.smartplatform.pojo.Lesson;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShowLessonsService {
    List<Lesson> findLessonsByUserId(int user_id);

    List<Lesson> findLessonsByUserIdAndSearchKey(int id, String searchKey);

    Lesson showLessonDetail(int id, int lessonId);

    List<Doc> getLessonsDocs(int id, int lessonId, int fatherId);

    ResponseEntity<Resource> downloadDocs(int id, int docsId);

    void updateLessonOutline(int id, int lessonId, String outline);

    void deleteLessonDocs(int id, int docsId);

    void updateDownloadLicense(int id, int docsId,int license);
}
