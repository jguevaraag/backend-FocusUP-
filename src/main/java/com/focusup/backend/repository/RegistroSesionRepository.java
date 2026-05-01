package com.focusup.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.focusup.backend.model.RegistroSesion;

@Repository
public interface RegistroSesionRepository extends JpaRepository<RegistroSesion, Long> {
}
