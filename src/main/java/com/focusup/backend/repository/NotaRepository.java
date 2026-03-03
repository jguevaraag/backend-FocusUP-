package com.focusup.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.focusup.backend.model.Nota;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    
    List<Nota> findByUsuariId(Long usuariId);
}
