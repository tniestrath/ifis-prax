package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoTrashcan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoTrashcanRepository extends JpaRepository<WPWPForoTrashcan, Long> {
}
