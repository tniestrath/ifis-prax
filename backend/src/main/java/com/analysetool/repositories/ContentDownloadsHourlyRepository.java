package com.analysetool.repositories;

import com.analysetool.modells.ContentDownloadsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentDownloadsHourlyRepository extends JpaRepository<ContentDownloadsHourly, Integer> {

}
