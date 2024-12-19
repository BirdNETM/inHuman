package org.inhuman.smartplatform.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.ShowLessonsMapper;
import org.inhuman.smartplatform.pojo.Doc;
import org.inhuman.smartplatform.pojo.Lesson;
import org.inhuman.smartplatform.service.ShowLessonsService;
import org.inhuman.smartplatform.utils.DatabaseUtil;
import org.inhuman.smartplatform.utils.DocsDownloadUtils;
import org.inhuman.smartplatform.utils.TutorPrivilegeCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
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
            int license = showLessonsMapper.getLicenseById(docsId);
            if(license == 0){
                return ResponseEntity.notFound().build();
            }
            String url = showLessonsMapper.getDocsUrlById(id, docsId);

            return DocsDownloadUtils.downloadDocsByUrl(url);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void updateLessonOutline(int id, int lessonId, String outline) throws Exception {
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,2);
        showLessonsMapper.updateLessonOutline(id,lessonId,outline);
    }

    @Override
    public void deleteLessonDocs(int id, int docsId) throws Exception {
        int lessonId = DatabaseUtil.getLessonByDocId(docsId);
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,2);

        // 获取所有直接子记录的 ID 列表
        List<Integer> childDocsIds = showLessonsMapper.getIdByFatherId(docsId);

        // 对每个子记录进行递归删除
        if(childDocsIds.size() > 0){
            for (Integer childId : childDocsIds) {
                // 删除子记录本身
                deleteLessonDocs(id,childId);
            }
        }

        String filepath = showLessonsMapper.getDocUrlById2(docsId);
        if (filepath == null) {
            throw new IllegalArgumentException("无法获取文件路径，id: " + id + ", docsId: " + docsId);
        }


        Path path = Paths.get(filepath);
        Files.deleteIfExists(path);
        showLessonsMapper.deleteLessonDocs(id, docsId);
    }

    @Override
    public void updateDownloadLicense(int id, int docsId, int license) throws Exception {
        int lessonId = DatabaseUtil.getLessonByDocId(docsId);
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,2);
        showLessonsMapper.updateDownloadLicense(id, docsId,license);
    }

    @Override
    public void insertDocs(int id, int lessonId, int docFatherId, int downloadLicense, MultipartFile file, String docName) throws Exception {
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,2);
        try {
            int docType = 1; // 默认文件类型为文件（docType=1）


            // 获取父路径，如果获取不到则抛出异常
            String fatherUrl = showLessonsMapper.getFatherUrl(docFatherId);
            if (fatherUrl == null || fatherUrl.isEmpty()) {
                throw new IllegalArgumentException("Father directory URL cannot be null or empty");
            }
            // 拼接完整的文件路径（使用 File.separator 而不是 "\\"，以便兼容不同操作系统）
            String docUrl = fatherUrl + File.separator + docName;

            // 判断上传的文件是否为空
            if (file == null || file.isEmpty()) {
                // 如果文件为空，创建一个以 `docName` 命名的目录
                File docDirectory = new File(docUrl);
                if (!docDirectory.exists() && !docDirectory.mkdirs()) {
                    throw new IOException("Failed to create directory: " + docUrl);
                }
                // 设置 docType 表示这是一个目录
                docType = 0;
                docUrl = docDirectory.getAbsolutePath(); // 更新 docUrl 为目录路径

            } else {
                // 如果文件不为空，将文件保存到指定位置
                File destinationFile = new File(docUrl);
                file.transferTo(destinationFile);
            }

            // 将文件或目录的元数据插入数据库
            showLessonsMapper.insertDocs(lessonId, docName, docType, docFatherId, docUrl, downloadLicense);

        } catch (IOException e) {
            // 捕获并处理任何文件操作异常
            e.printStackTrace();
            throw new RuntimeException("Failed to save file or create directory: " + docName, e);
        }
    }

    @Override
    public void insertDirectory(int id,int lessonId, int docFatherId, int downloadLicense, String docName) throws Exception {
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,2);
        try{
            String fatherUrl = showLessonsMapper.getFatherUrl(docFatherId);
            if (fatherUrl == null || fatherUrl.isEmpty()) {
                throw new IllegalArgumentException("Father directory URL cannot be null or empty");
            }
            // 拼接完整的文件路径（使用 File.separator 而不是 "\\"，以便兼容不同操作系统）
            String docUrl = fatherUrl + File.separator + docName;
            File docDirectory = new File(docUrl);
            if (!docDirectory.exists() && !docDirectory.mkdirs()) {
                throw new IOException("Failed to create directory: " + docUrl);
            }
            // 设置 docType 表示这是一个目录
            showLessonsMapper.insertDocs(lessonId, docName, 0, docFatherId, docUrl, downloadLicense);
        }
       catch (IOException e) {
            // 捕获并处理任何文件操作异常
            e.printStackTrace();
            throw new RuntimeException("Failed to save file or create directory: " + docName, e);
        }
    }
}
