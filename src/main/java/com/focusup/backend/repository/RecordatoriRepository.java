package com.focusup.backend.repository;

import com.focusup.backend.model.Recordatori;
import com.focusup.backend.model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordatoriRepository extends JpaRepository<Recordatori, Long> {

    List<Recordatori> findByUsuari(Usuari usuari);
    
    List<Recordatori> findByUsuariAndDataHoraBetween(Usuari usuari, LocalDateTime start, LocalDateTime end);

    List<Recordatori> findByDataHoraBeforeAndCompletatFalseAndPenalitzatFalse(LocalDateTime data);

    long countByUsuariAndCompletatTrue(Usuari usuari);

    List<Recordatori> findByGrupId(Long grupId);
}
