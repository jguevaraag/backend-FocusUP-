package com.focusup.backend.controller;


import com.focusup.backend.dto.RankingDTO;
import com.focusup.backend.model.Grup;
import com.focusup.backend.model.GrupUsuari;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.GrupRepository;
import com.focusup.backend.repository.GrupUsuariRepository;
import com.focusup.backend.repository.SessioEstudiRepository;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grups")
public class GrupController {

    @Autowired
    private GrupRepository grupRepository;

    @Autowired
    private GrupUsuariRepository grupUsuariRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private SessioEstudiRepository sessioRepository;

    // Helper para sacar el usuario actual
    private Usuari getUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return usuariRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // 1. CREAR UN GRUPO
    @PostMapping
    public ResponseEntity<?> crearGrup(@RequestBody Map<String, String> request) {
        Usuari usuari = getUsuarioActual();
        String nombreGrupo = request.get("nom");

        if (nombreGrupo == null || nombreGrupo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre del grupo es obligatorio"));
        }

        // Creamos el grupo y generamos un código aleatorio de 6 caracteres (ej: A7B9F2)
        Grup nuevoGrup = new Grup();
        nuevoGrup.setNom(nombreGrupo);
        String codiSecreto = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        nuevoGrup.setCodiAcces(codiSecreto);
        
        Grup grupGuardado = grupRepository.save(nuevoGrup);

        // Añadimos al creador como el primer miembro del grupo
        GrupUsuari union = new GrupUsuari();
        union.setGrup(grupGuardado);
        union.setUsuari(usuari);
        union.setDataUnio(LocalDateTime.now());
        grupUsuariRepository.save(union);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Grupo creado exitosamente",
                "grup_id", grupGuardado.getId(),
                "codi_acces", codiSecreto
        ));
    }

    // 2. UNIRSE A UN GRUPO CON EL CÓDIGO
    @PostMapping("/join")
    public ResponseEntity<?> unirseGrup(@RequestBody Map<String, String> request) {
        Usuari usuari = getUsuarioActual();
        String codi = request.get("codi_acces");

        Grup grup = grupRepository.findByCodiAcces(codi)
                .orElseThrow(() -> new RuntimeException("Código de grupo inválido o no existe"));

        // Comprobamos si ya está dentro
        if (grupUsuariRepository.existsByUsuariAndGrupId(usuari, grup.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ya perteneces a este grupo"));
        }

        // Le metemos en el grupo
        GrupUsuari nuevaUnion = new GrupUsuari();
        nuevaUnion.setGrup(grup);
        nuevaUnion.setUsuari(usuari);
        nuevaUnion.setDataUnio(LocalDateTime.now());
        grupUsuariRepository.save(nuevaUnion);

        return ResponseEntity.ok(Map.of("mensaje", "Te has unido al grupo " + grup.getNom()));
    }

    // 3. VER MIS GRUPOS
    @GetMapping("/me")
    public ResponseEntity<?> obtenerMisGrupos() {
        Usuari usuari = getUsuarioActual();
        
        // Extraemos los datos limpios en un Map para evitar problemas de serialización con Hibernate
        List<Map<String, Object>> misGrupos = grupUsuariRepository.findByUsuari(usuari)
                .stream()
                .map(union -> {
                    Grup g = union.getGrup();
                    return Map.<String, Object>of(
                            "id", g.getId(),
                            "nom", g.getNom(),
                            "codiAcces", g.getCodiAcces()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(misGrupos);
    }

    @GetMapping("/{grupId}/ranking")
    public ResponseEntity<?> obtenerRankingDelGrupo(@PathVariable Long grupId) {
        Usuari usuariActual = getUsuarioActual();

        // 1. Seguridad: Comprobar que el usuario pertenece a este grupo
        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para ver el ranking de este grupo"));
        }

        // 2. Obtener los usuarios del grupo ordenados por puntos
        List<Usuari> usuariosOrdenados = grupUsuariRepository.findRankingByGrupId(grupId);

        // 3. Mapear a RankingDTO para ocultar contraseñas y emails (Reaprovechamos tu DTO)
        List<RankingDTO> ranking = usuariosOrdenados.stream()
                .map(u -> new RankingDTO(u.getUsername(), u.getPunts()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/{grupId}/stats")
    public ResponseEntity<?> obtenerEstadisticasGrupo(@PathVariable Long grupId) {
        Usuari usuariActual = getUsuarioActual();

        // 1. Seguridad: Solo los miembros del grupo pueden ver estos datos
        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para ver las estadísticas de este grupo"));
        }

        // 2. Calculamos el total de minutos de todo el equipo
        Integer minutosTotales = sessioRepository.sumarMinutosTotalesDelGrupo(grupId);

        // Opcional: Podríais definir la "Meta" aquí, o dejar que el frontend la decida.
        int metaSemanal = 1000; 

        return ResponseEntity.ok(Map.of(
                "minutos_estudiados_grupo", minutosTotales,
                "meta_semanal", metaSemanal
        ));
    }
}