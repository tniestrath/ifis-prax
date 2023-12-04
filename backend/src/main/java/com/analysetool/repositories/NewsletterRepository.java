package com.analysetool.repositories;

import com.analysetool.modells.Newsletter;
import com.analysetool.modells.PostMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    @Query("SELECT n.status FROM Newsletter n WHERE n.id = :id")
    public char getStatusById(Long id);

    @Query("SELECT n.status FROM Newsletter n")
    public List<Character> getStatusAll();

    @Query("SELECT n.status FROM Newsletter n WHERE n.email = :umail")
    public char getStatusByMail(String umail);

    @Query("SELECT n.email FROM Newsletter n WHERE n.status = :ustatus")
    public List<String> getMailsByStatus(char ustatus);

    @Query("SELECT n.email, n.status FROM Newsletter n")
    public Map<String, Character> getMailAndStatusAll();
}
