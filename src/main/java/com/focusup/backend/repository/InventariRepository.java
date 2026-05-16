package com.focusup.backend.repository;

import com.focusup.backend.model.Inventari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventariRepository extends JpaRepository<Inventari, Long> {

    List<Inventari> findByUsuariId(Long usuariId);

    Optional<Inventari> findByUsuariIdAndItemId(Long usuariId, Long itemId);
    
    @Query("SELECT i FROM Inventari i WHERE i.usuari.id = :usuariId AND i.item.tipus = :tipus AND i.equipado = true")
    List<Inventari> buscarEquipadosPorTipo(@Param("usuariId") Long usuariId, @Param("tipus") String tipus);
}