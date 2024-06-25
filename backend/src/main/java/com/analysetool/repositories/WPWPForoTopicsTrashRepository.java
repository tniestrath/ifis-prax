package com.analysetool.repositories;

import com.analysetool.modells.WPWPForoTopicsTrash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WPWPForoTopicsTrashRepository extends JpaRepository<WPWPForoTopicsTrash, Long> {

}
