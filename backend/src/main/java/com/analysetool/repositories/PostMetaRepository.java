package com.analysetool.repositories;

import com.analysetool.modells.PostMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMetaRepository extends JpaRepository<PostMeta, Long> {

    @Query("SELECT p.meta_value FROM PostMeta p WHERE p.post_id =:postID AND p.meta_key='ppma_authors_name'")
    String getAuthorsByPostId(Long postID);

    @Query("SELECT p.meta_value FROM PostMeta p WHERE p.meta_key='audio_podcast' AND p.post_id=:postID")
    String getFilePath(Long postID);

    @Query("SELECT p.meta_value FROM PostMeta p WHERE p.meta_key='um_whitepaper_pdf'")
    List<Long> getAllWhitepaperFileAttachmentPostIds();

    @Query("SELECT DISTINCT p.meta_value FROM PostMeta p WHERE p.post_id=:postId AND p.meta_key='ppma_authors_name'")
    List<String> getAuthorsList(long postId);

    @Query("SELECT p.meta_value FROM PostMeta p WHERE p.post_id = (SELECT CAST(pm.meta_value as int) FROM PostMeta pm WHERE pm.meta_key='_thumbnail_id' AND pm.post_id=:id) AND p.meta_key='_wp_attached_file'")
    String getThumbnail(long id);

}