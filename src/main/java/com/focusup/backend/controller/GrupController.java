package com.focusup.backend.controller;

import com.focusup.backend.dto.NotaRequest;
import com.focusup.backend.dto.RankingDTO;
import com.focusup.backend.dto.RecordatoriRequest;
import com.focusup.backend.model.Grup;
import com.focusup.backend.model.GrupUsuari;
import com.focusup.backend.model.Nota;
import com.focusup.backend.model.Recordatori;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.GrupRepository;
import com.focusup.backend.repository.GrupUsuariRepository;
import com.focusup.backend.repository.NotaRepository;
import com.focusup.backend.repository.RecordatoriRepository;
import com.focusup.backend.repository.SessioEstudiRepository;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
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

    @Autowired
    private RecordatoriRepository recordatoriRepository;

    @Autowired
    private NotaRepository notaRepository;

    private Usuari getUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return usuariRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> crearGrup(@RequestBody Map<String, String> request) {
        Usuari usuari = getUsuarioActual();
        String nombreGrupo = request.get("nom");

        if (nombreGrupo == null || nombreGrupo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre del grupo es obligatorio"));
        }

        Grup nuevoGrup = new Grup();
        nuevoGrup.setNom(nombreGrupo);
        String codiSecreto = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        nuevoGrup.setCodiAcces(codiSecreto);

        Grup grupGuardado = grupRepository.save(nuevoGrup);

        GrupUsuari union = new GrupUsuari();
        union.setGrup(grupGuardado);
        union.setUsuari(usuari);
        union.setDataUnio(LocalDateTime.now());
        grupUsuariRepository.save(union);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Grupo creado exitosamente",
                "grup_id", grupGuardado.getId(),
                "codi_acces", codiSecreto));
    }

    @PostMapping("/join")
    public ResponseEntity<?> unirseGrup(@RequestBody Map<String, String> request) {
        Usuari usuari = getUsuarioActual();
        String codi = request.get("codi_acces");

        Grup grup = grupRepository.findByCodiAcces(codi)
                .orElseThrow(() -> new RuntimeException("Código de grupo inválido o no existe"));

        if (grupUsuariRepository.existsByUsuariAndGrupId(usuari, grup.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ya perteneces a este grupo"));
        }

        GrupUsuari nuevaUnion = new GrupUsuari();
        nuevaUnion.setGrup(grup);
        nuevaUnion.setUsuari(usuari);
        nuevaUnion.setDataUnio(LocalDateTime.now());
        grupUsuariRepository.save(nuevaUnion);

        return ResponseEntity.ok(Map.of("mensaje", "Te has unido al grupo " + grup.getNom()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> obtenerMisGrupos() {
        Usuari usuari = getUsuarioActual();

        List<Map<String, Object>> misGrupos = grupUsuariRepository.findByUsuari(usuari)
                .stream()
                .map(union -> {
                    Grup g = union.getGrup();
                    return Map.<String, Object>of(
                            "id", g.getId(),
                            "nom", g.getNom(),
                            "codiAcces", g.getCodiAcces());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(misGrupos);
    }

    @GetMapping("/{grupId}/ranking")
    public ResponseEntity<?> obtenerRankingDelGrupo(@PathVariable Long grupId) {
        Usuari usuariActual = getUsuarioActual();

        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "No tienes permiso para ver el ranking de este grupo"));
        }

        List<Usuari> usuariosOrdenados = grupUsuariRepository.findRankingByGrupId(grupId);
        List<RankingDTO> ranking = usuariosOrdenados.stream()
                .map(u -> new RankingDTO(u.getUsername(), u.getPunts()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/{grupId}/stats")
    public ResponseEntity<?> obtenerEstadisticasGrupo(@PathVariable Long grupId) {
        Usuari usuariActual = getUsuarioActual();

        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "No tienes permiso para ver las estadísticas de este grupo"));
        }

        Integer minutosTotales = sessioRepository.sumarMinutosTotalesDelGrupo(grupId);

        int metaSemanal = 1000;

        return ResponseEntity.ok(Map.of(
                "minutos_estudiados_grupo", minutosTotales,
                "meta_semanal", metaSemanal));
    }

    @GetMapping("/{grupId}/recordatoris")
    public ResponseEntity<?> obtenerRecordatoriosGrupo(@PathVariable Long grupId) {
        Usuari usuariActual = getUsuarioActual();

        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para ver este grupo"));
        }

        List<Map<String, Object>> respuesta = recordatoriRepository.findByGrupId(grupId).stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("missatge", r.getMissatge());
            map.put("dataHora", r.getDataHora());
            map.put("completat", r.isCompletat());
            map.put("creadorNom", r.getUsuari() != null ? r.getUsuari().getNom() : "Sistema");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/{grupId}/recordatoris")
    public ResponseEntity<?> crearRecordatoriGrupo(@PathVariable Long grupId, @RequestBody RecordatoriRequest request) {
        Usuari usuariActual = getUsuarioActual();
        Grup grup = grupRepository.findById(grupId).orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No puedes crear tareas aquí"));
        }

        Recordatori recordatori = new Recordatori();
        recordatori.setMissatge(request.getMissatge());
        recordatori.setDataHora(request.getDataHora());
        recordatori.setUsuari(usuariActual);
        recordatori.setGrup(grup);

        Recordatori guardado = recordatoriRepository.save(recordatori);
        return ResponseEntity.ok(Map.of("id", guardado.getId(), "mensaje", "Recordatorio de grupo creado"));
    }

    @GetMapping("/{grupId}/notas")
    public ResponseEntity<?> obtenerNotasGrupo(@PathVariable Long grupId) {
        Usuari usuariActual = getUsuarioActual();

        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }

        List<Map<String, Object>> respuesta = notaRepository.findByGrupId(grupId).stream().map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("titol", n.getTitol());
            map.put("contingut", n.getContingut());
            map.put("data", n.getData());
            map.put("creadorNom", n.getUsuari() != null ? n.getUsuari().getNom() : "Anónimo");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/{grupId}/notas")
    public ResponseEntity<?> crearNotaGrupo(@PathVariable Long grupId, @RequestBody NotaRequest request) {
        Usuari usuariActual = getUsuarioActual();
        Grup grup = grupRepository.findById(grupId).orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        if (!grupUsuariRepository.existsByUsuariAndGrupId(usuariActual, grupId)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }

        Nota nuevaNota = new Nota();
        nuevaNota.setTitol(request.getTitol());
        nuevaNota.setContingut(request.getContingut());
        nuevaNota.setData(request.getData() != null ? request.getData() : LocalDate.now());
        nuevaNota.setUsuari(usuariActual);
        nuevaNota.setGrup(grup);

        notaRepository.save(nuevaNota);
        return ResponseEntity.ok(Map.of("mensaje", "Nota compartida guardada"));
    }

    @PatchMapping("/{grupId}/recordatoris/{id}/estat")
    public ResponseEntity<?> cambiarEstadoRecordatorioGrupo(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Recordatori r = recordatoriRepository.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
        if (request.containsKey("completat")) {
            r.setCompletat(request.get("completat"));
        }
        recordatoriRepository.save(r);
        return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado"));
    }
}