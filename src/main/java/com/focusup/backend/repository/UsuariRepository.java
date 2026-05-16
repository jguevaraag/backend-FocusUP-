package com.focusup.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.focusup.backend.model.Usuari;

@Repository
public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    Optional<Usuari> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
