package com.focusup.backend.repository;

import com.focusup.backend.model.Inventari;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventariRepository extends JpaRepository<Inventari, Long> {

    
    List<Inventari> findByUsuariId(Long usuariId);

    // Ver si el usuario YA tiene un item específico (Para que no lo compre dos veces).
    Optional<Inventari> findByUsuariIdAndItemId(Long usuariId, Long itemId);
    
}