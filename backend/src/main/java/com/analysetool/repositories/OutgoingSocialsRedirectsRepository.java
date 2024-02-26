package com.analysetool.repositories;

import com.analysetool.modells.OutgoingSocialsRedirects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutgoingSocialsRedirectsRepository  extends JpaRepository<OutgoingSocialsRedirects, Long> {


    Optional<OutgoingSocialsRedirects> findByUniIdAndHour(Integer uniId,Integer hour);

}
