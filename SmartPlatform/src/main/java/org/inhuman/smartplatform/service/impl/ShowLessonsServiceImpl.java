package org.inhuman.smartplatform.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.ShowLessonsMapper;
import org.inhuman.smartplatform.pojo.Doc;
import org.inhuman.smartplatform.pojo.Lesson;
import org.inhuman.smartplatform.service.ShowLessonsService;
import org.inhuman.smartplatform.utils.DocsDownloadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ShowLessonsServiceImpl implements ShowLessonsService {

    @Autowired
    private ShowLessonsMapper showLessonsMapper;

    @Override
    public List<Lesson> findLessonsByUserId(int user_id) {
        // 调用 Mapper 层获取用户的课程
        return showLessonsMapper.findLessonsByUserId(user_id);
    }

    @Override
    public List<Lesson> findLessonsByUserIdAndSearchKey(int user_id, String searchKey){
        return showLessonsMapper.findLessonsByUserIdAndSearchKey(user_id,searchKey);
    }

    @Override
    public Lesson showLessonDetail(int id, int lessonId) {
        return showLessonsMapper.showLessonDetail(id,lessonId);
    }

    @Override
    public List<Doc> getLessonsDocs(int id, int lessonId, int fatherId) {
        return showLessonsMapper.getLessonsDocs(id,lessonId,fatherId);
    }

    @Override
    public ResponseEntity<Resource> downloadDocs(int id, int docsId) {
        try {
            String url = showLessonsMapper.getDocsUrlById(id, docsId);
            return DocsDownloadUtils.downloadDocsByUrl(url);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public void updateLessonOutline(int id, int lessonId, String outline) {
        showLessonsMapper.updateLessonOutline(id,lessonId,outline);
    }

    @Override
    public void deleteLessonDocs(int id, int docsId) {
        showLessonsMapper.deleteLessonDocs(id, docsId);
        showLessonsMapper.deleteLessonDocs(id, docsId);
    }

    @Override
    public void updateDownloadLicense(int id, int docsId, int license) {
        showLessonsMapper.updateDownloadLicense(id, docsId,license);
    }
}
