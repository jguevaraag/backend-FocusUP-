package com.focusup.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.focusup.backend.model.SessioEstudi;

public interface SessioEstudiRepository extends JpaRepository<SessioEstudi, Long> {

    List<SessioEstudi> findByUsuariId(Long usuariId);

    @Query("SELECT COALESCE(SUM(s.duracioMinuts), 0) FROM SessioEstudi s WHERE s.usuari.id = :usuariId")
    Integer sumarMinutosPorUsuario(@Param("usuariId") Long usuariId);

    @Query("SELECT COALESCE(SUM(s.duracioMinuts), 0) FROM SessioEstudi s WHERE s.usuari.id IN (SELECT gu.usuari.id FROM GrupUsuari gu WHERE gu.grup.id = :grupId)")
    Integer sumarMinutosTotalesDelGrupo(@Param("grupId") Long grupId);
}
