package com.analysetool.repositories;

import com.analysetool.modells.ContentDownloadsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentDownloadsHourlyRepository extends JpaRepository<ContentDownloadsHourly, Integer> {
    List<ContentDownloadsHourly> findAllByPostId(Long postId);
    List<ContentDownloadsHourly> findAllByPostIdAndUniId(Long postId,Integer uniId);
}
