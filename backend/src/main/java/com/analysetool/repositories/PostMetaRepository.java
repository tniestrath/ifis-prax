package com.analysetool.repositories;

import com.analysetool.modells.PostMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMetaRepository extends JpaRepository<PostMeta, Long> {

    @Query("SELECT p.meta_value FROM PostMeta p WHERE p.post_id =:postID AND p.meta_key='ppma_authors_name'")
    public String getAuthorsByPostId(Long postID);

    @Query("SELECT p.meta_value FROM PostMeta p WHERE p.meta_key='audio_podcast' AND p.post_id=:postID")
    String getFilePath(Long postID);

}