package com.analysetool.repositories;

import com.analysetool.modells.PostMeta;
import com.analysetool.modells.UniqueUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UniqueUserRepository extends JpaRepository<UniqueUser, Long> {

    @Query("SELECT u.category FROM UniqueUser u WHERE u.id = :id")
    public String getCategoryByID(int id);

    @Query("SELECT count(u.ip) FROM UniqueUser u WHERE u.category =:category")
    public int getUserCountByCategory(String category);

    @Query("SELECT count(u.ip) FROM UniqueUser u")
    public int getUserCountGlobal();

    @Query("SELECT u FROM UniqueUser u WHERE u.ip = :ip")
    public UniqueUser findByIP(String ip);

    @Modifying
    @Transactional
    @Query("DELETE FROM UniqueUser u")
    void truncate();


}
