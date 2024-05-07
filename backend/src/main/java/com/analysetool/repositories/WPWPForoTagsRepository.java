package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoTagsRepository extends JpaRepository<WPWPForoTags, Long> {
}
