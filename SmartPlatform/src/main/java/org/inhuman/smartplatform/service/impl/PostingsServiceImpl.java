package org.inhuman.smartplatform.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.inhuman.smartplatform.mapper.PostingsMapper;
import org.inhuman.smartplatform.pojo.Postings;
import org.inhuman.smartplatform.service.PostingsService;
import org.inhuman.smartplatform.utils.DocsDownloadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@Slf4j
@Service
public class PostingsServiceImpl implements PostingsService {

    @Autowired
    PostingsMapper postingsMapper;


    @Override
    public void setPosting(int id, int lessonId, Postings postings) {
        postingsMapper.setPosting(id,lessonId,postings);
    }

    @Override
    public void insertPostingPicture(int id, int postingId, int pictureId, MultipartFile file) {
        String filePath;
        try {
            filePath = "D://postings/" + postingId + "/" + pictureId;
            DocsDownloadUtils.uploadDocsByUrl(file, filePath);
        } catch (Exception e) {
            // 可以根据具体需求处理异常，如打印日志或抛出自定义异常
            log.error(e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
        postingsMapper.insertPostingPicture(id, postingId, pictureId, filePath);
    }

    @Override
    public Postings getPostingDetailById(int id, int postingId) {
        //log.info(String.valueOf(postingsMapper.getPostingDetailById(id,postingId)));
        return postingsMapper.getPostingDetailById(id,postingId);
    }

    @Override
    public ResponseEntity<Resource> getPostingPicturesById(int id, int postingId, int pictureId) throws MalformedURLException {
        return DocsDownloadUtils.getImage(postingsMapper.getPostingPicturesById(id,postingId,pictureId));
    }


}
