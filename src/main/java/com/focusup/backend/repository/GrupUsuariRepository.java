package com.focusup.backend.repository;


import com.focusup.backend.model.GrupUsuari;
import com.focusup.backend.model.GrupUsuariID;
import com.focusup.backend.model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GrupUsuariRepository extends JpaRepository<GrupUsuari, GrupUsuariID> {
    List<GrupUsuari> findByUsuari(Usuari usuari);
    boolean existsByUsuariAndGrupId(Usuari usuari, Long grupId); // Para evitar que se unan 2 veces

    @Query("SELECT gu.usuari FROM GrupUsuari gu WHERE gu.grup.id = :grupId ORDER BY gu.usuari.punts DESC")
    List<Usuari> findRankingByGrupId(@Param("grupId") Long grupId);
}