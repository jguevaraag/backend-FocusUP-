package com.focusup.backend.repository;

import com.focusup.backend.model.Grup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GrupRepository extends JpaRepository<Grup, Long> {
    Optional<Grup> findByCodiAcces(String codiAcces);
}