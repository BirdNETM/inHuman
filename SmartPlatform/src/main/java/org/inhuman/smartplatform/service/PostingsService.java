package org.inhuman.smartplatform.service;


import org.inhuman.smartplatform.pojo.Postings;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@Service
public interface PostingsService {
    int setPosting(int id, int lessonId, Postings postings);

    void insertPostingPicture(int id, int postingId, int pictureId, MultipartFile file);

    Postings getPostingDetailById(int id, int postingId);

    ResponseEntity<Resource> getPostingPicturesById(int id, int postingId, int pictureId) throws MalformedURLException;

    List<Postings> getPostings(int id, int lessonId);

    List<Postings> getPostingsByTopic(int id, String topic);
}
