package com.analysetool.repositories;

import com.analysetool.modells.UniqueUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UniqueUserRepository extends JpaRepository<UniqueUser, Long> {

    @Query("SELECT count(u.ip) FROM UniqueUser u")
    public int getUserCountGlobal();

    @Query("SELECT u FROM UniqueUser u WHERE u.ip = :ip")
    public UniqueUser findByIP(String ip);
}
