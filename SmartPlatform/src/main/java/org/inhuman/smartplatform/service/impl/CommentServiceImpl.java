package org.inhuman.smartplatform.service.impl;

import org.inhuman.smartplatform.mapper.CommentMapper;
import org.inhuman.smartplatform.pojo.Comment;
import org.inhuman.smartplatform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Override
    public List<Comment> getComments(int id, int postingId) {
        return commentMapper.getComments(id, postingId);
    }

    @Override
    public void setComments(int id, Comment comment) {
        commentMapper.setComments(id, comment);
    }
}
