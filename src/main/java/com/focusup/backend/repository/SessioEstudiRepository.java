package com.focusup.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.focusup.backend.model.SessioEstudi;

public interface SessioEstudiRepository extends JpaRepository<SessioEstudi, Long> {

    List<SessioEstudi> findByUsuariId(Long usuariId);
}
