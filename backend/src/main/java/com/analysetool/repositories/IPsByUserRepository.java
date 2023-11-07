package com.analysetool.repositories;

import com.analysetool.modells.IPsByUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPsByUserRepository extends JpaRepository<IPsByUser, Long> {

}
