package com.analysetool.repositories;

import com.analysetool.modells.IPsByUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPsByUserRepository extends JpaRepository<IPsByUser, Long> {

    @Query("SELECT u FROM IPsByUser u WHERE u.user_id=:id")
    public IPsByUser getByID(long id);
}
