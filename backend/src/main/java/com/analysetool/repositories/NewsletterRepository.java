package com.analysetool.repositories;

import com.analysetool.modells.Newsletter;
import com.analysetool.modells.PostMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    @Query("SELECT n.status FROM Newsletter n WHERE n.id = :id")
    public char getStatusById(Long id);

    @Query("SELECT n.status FROM Newsletter n")
    public List<Character> getStatusAll();



}
