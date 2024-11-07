package org.inhuman.smartplatform.service;

import org.inhuman.smartplatform.pojo.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    List<Comment> getComments(int id, int postingId);

    void setComments(int id, Comment comment);
}
