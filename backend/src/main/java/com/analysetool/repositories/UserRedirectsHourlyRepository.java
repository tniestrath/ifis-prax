package com.analysetool.repositories;


import com.analysetool.modells.UserRedirectsHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedirectsHourlyRepository extends JpaRepository<UserRedirectsHourly, Integer> {
    // Additional query methods can be defined here
}
