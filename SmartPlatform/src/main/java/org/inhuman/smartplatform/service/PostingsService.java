package org.inhuman.smartplatform.service;


import org.inhuman.smartplatform.pojo.Postings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface PostingsService {
    void setPosting(int id, int lessonId, Postings postings);

    void insertPostingPicture(int id, int postingId, int pictureId, MultipartFile file);

    void getPostingDetailById(int id, int postingId);
}
