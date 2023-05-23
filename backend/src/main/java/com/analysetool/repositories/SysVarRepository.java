package com.analysetool.repositories;

import com.analysetool.modells.SysVar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysVarRepository extends JpaRepository<SysVar, Integer> {
    // Benutzerdefinierte Repository-Methoden können hier hinzugefügt werden
}

