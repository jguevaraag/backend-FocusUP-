package com.focusup.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.focusup.backend.model.Nota;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    
    List<Nota> findByUsuariId(Long usuariId);

    List<Nota> findByDataAndUsuariId(LocalDate data, Long usuariId);

    boolean existsBySessioEstudiId(Long sessioId);

    List<Nota> findByGrupId(Long grupId);
}
