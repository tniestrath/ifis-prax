package com.analysetool.repositories;

import com.analysetool.modells.Comments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CommentsRepository extends CrudRepository<Comments, Long> {

    List<Comments> findByPostId(Long postId);
    Comments findByCommentId(Long commentId);
}

