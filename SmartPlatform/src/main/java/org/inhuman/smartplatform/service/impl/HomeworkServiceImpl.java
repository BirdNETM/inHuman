package org.inhuman.smartplatform.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.HomeworkMapper;
import org.inhuman.smartplatform.pojo.Homework;
import org.inhuman.smartplatform.service.HomeworkService;
import org.inhuman.smartplatform.utils.DatabaseUtil;
import org.inhuman.smartplatform.utils.DocsDownloadUtils;
import org.inhuman.smartplatform.utils.TutorPrivilegeCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.inhuman.smartplatform.utils.DatabaseUtil.getLessonByHomeworkId;

@Slf4j
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    HomeworkMapper homeworkMapper;
    @Autowired
    private View error;


    @Override
    public List<Homework> getHomeworks(int id, int lessonId) {
        return homeworkMapper.getHomeworks(id,lessonId);
    }

    @Override
    public ResponseEntity<Resource> downloadHomeworkFiles(int id, int homeworkId) {
        try {
            String url = homeworkMapper.getDocsUrlById(id, homeworkId);
            return DocsDownloadUtils.downloadDocsByUrl(url);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Homework getHomeworksById(int id, int homeworkId) {
        return homeworkMapper.getHomeworksById(id,homeworkId);
    }

    @Override
    public int publishHomework(int id, int lessonId, Homework homework) throws Exception {
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,1);
        homeworkMapper.publishHomework(id,lessonId,homework);
        int homeworkId = homeworkMapper.getLastInsertId();
        List<Integer> students = homeworkMapper.getStudentsInLesson(lessonId);
        for(Integer student : students){
            homeworkMapper.homeworkToStudents(student,homeworkId);
        }
        return homeworkId;
    }

    @Override
    public void publishHomeworkFile(int id, int homeworkId, MultipartFile file) throws Exception {
        int lessonId = Integer.parseInt(DatabaseUtil.getLessonByHomeworkId(homeworkId));
        log.info("lessonId = " + lessonId);
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,1);
        String filePath;
        try {
            filePath = "D://InHumanFile//homeworkFiles/" + homeworkId + "/" + file.getOriginalFilename();
            DocsDownloadUtils.uploadDocsByUrl(file, filePath);
        } catch (Exception e) {
            // 可以根据具体需求处理异常，如打印日志或抛出自定义异常
            log.error(e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
        homeworkMapper.publishHomeworkFile(id, homeworkId, filePath);
    }


    @Override
    public void submitHomework(int id, int homeworkId, MultipartFile file) throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime overTime = homeworkMapper.getOverTime(homeworkId);

        // 超时检查
        if (currentTime.isAfter(overTime)) {
            throw new Exception("超时，无法提交作业"); // 使用自定义异常
        }

        String lastUrl = homeworkMapper.getSubmittedUrl(id, homeworkId);
        log.info("上次提交的文件路径: {}", lastUrl);

        // 尝试删除之前提交的文件
        if (lastUrl != null) {
            Path path = Paths.get(lastUrl);
            if (Files.exists(path)) {
                try {
                    log.info("尝试删除文件的路径: {}", lastUrl);
                    Files.delete(path);
                } catch (IOException e) {
                    log.error("删除文件失败: {}", e.getMessage(), e);
                    throw new RuntimeException("删除之前提交的文件失败", e);
                }
            } else {
                log.warn("文件不存在，无法删除: {}", lastUrl);
            }
        }

        String filePath = "D://InHumanFile//submitHomeworkFiles/" + homeworkId + "/" + id + "/" + file.getOriginalFilename();

        try {
            // 上传文件
            DocsDownloadUtils.uploadDocsByUrl(file, filePath);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
        // 更新数据库记录
        try {
            homeworkMapper.submitHomeworkAgain(id, homeworkId, filePath);
            log.info("id = {}, homeworkId = {}", id, homeworkId);
            homeworkMapper.updateHomeworkStatus(id, homeworkId);
        } catch (Exception e) {
            log.error("更新数据库记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据库更新失败", e);
        }
    }

    @Override
    public void updateHomework(int id, int homeworkId, Homework homework) throws Exception {
        int lessonId = Integer.parseInt(DatabaseUtil.getLessonByHomeworkId(homeworkId));
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,1);

        homeworkMapper.updateHomework(id,homeworkId,homework);
    }

    @Override
    public void updateHomeworkFile(int id, int homeworkId, MultipartFile file) throws Exception {
        int lessonId = Integer.parseInt(DatabaseUtil.getLessonByHomeworkId(homeworkId));
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,1);

        String lastUrl = homeworkMapper.getHomeworkFileUrl(id, homeworkId);
        log.info("上次提交的文件路径: {}", lastUrl);

        // 尝试删除之前提交的文件
        if (lastUrl != null) {
            Path path = Paths.get(lastUrl);
            if (Files.exists(path)) {
                try {
                    log.info("尝试删除文件的路径: {}", lastUrl);
                    Files.delete(path);
                } catch (IOException e) {
                    log.error("删除文件失败: {}", e.getMessage(), e);
                    throw new RuntimeException("删除之前提交的文件失败", e);
                }
            } else {
                log.warn("文件不存在，无法删除: {}", lastUrl);
            }
        }

        String filePath = "D://InHumanFile//homeworkFiles/" + homeworkId + "/"  + file.getOriginalFilename();

        try {
            // 上传文件
            DocsDownloadUtils.uploadDocsByUrl(file, filePath);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
        // 更新数据库记录
        try {
            homeworkMapper.setHomeworkFile(id, homeworkId, filePath);
        } catch (Exception e) {
            log.error("更新数据库记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据库更新失败", e);
        }
    }

    @Override
    public void deleteHomework(int id, int homeworkId) throws Exception {
        int lessonId = Integer.parseInt(DatabaseUtil.getLessonByHomeworkId(homeworkId));
        TutorPrivilegeCheckUtil.checkHomeworkPrivilege(id,lessonId,1);

        String lastUrl = homeworkMapper.getHomeworkFileUrl(id, homeworkId);
        log.info("上次提交的文件路径: {}", lastUrl);

        // 尝试删除之前提交的文件
        if (lastUrl != null) {
            Path path = Paths.get(lastUrl);
            if (Files.exists(path)) {
                try {
                    log.info("尝试删除文件的路径: {}", lastUrl);
                    Files.delete(path);
                } catch (IOException e) {
                    log.error("删除文件失败: {}", e.getMessage(), e);
                    throw new RuntimeException("删除之前提交的文件失败", e);
                }
            } else {
                log.warn("文件不存在，无法删除: {}", lastUrl);
            }
        }
        homeworkMapper.deleteHomework(id, homeworkId);
        homeworkMapper.deleteStudentHomework(id, homeworkId);
    }

    @Override
    public List<String> getStudentsNoSubmitHomework(int id, int homeworkId) {
        return homeworkMapper.getStudentsNoSubmitHomework(id, homeworkId);
    }

}
