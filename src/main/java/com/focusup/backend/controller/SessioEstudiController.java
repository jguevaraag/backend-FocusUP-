package com.focusup.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.focusup.backend.dto.SessioRequest;
import com.focusup.backend.model.Recordatori;
import com.focusup.backend.model.SessioEstudi;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.RecordatoriRepository;
import com.focusup.backend.repository.SessioEstudiRepository;
import com.focusup.backend.repository.UsuariRepository;

@RestController
@RequestMapping("/api/sessions")
public class SessioEstudiController {

    @Autowired
    private SessioEstudiRepository sessioRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private RecordatoriRepository recordatoriRepository; 

    @PostMapping
    public ResponseEntity<?> guardarSesion(@RequestBody SessioRequest request, Principal principal) {
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (request.getMinuts() == null || request.getMinuts() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Los minutos deben ser mayor a 0"));
        }

        SessioEstudi sesion = new SessioEstudi();
        sesion.setDuracioMinuts(request.getMinuts());

        LocalDateTime ahora = LocalDateTime.now();
        sesion.setDataFi(ahora);
        sesion.setDataInici(ahora.minusMinutes(request.getMinuts()));
        sesion.setUsuari(usuario);

        if (request.getRecordatoriId() != null) {
            Recordatori rec = recordatoriRepository.findById(request.getRecordatoriId()).orElse(null);

            if (rec != null && rec.getUsuari().getId().equals(usuario.getId())) {
                sesion.setRecordatori(rec);
                rec.setCompletat(true);
                recordatoriRepository.save(rec);
            }
        }

        sessioRepository.save(sesion);

        int puntosGanados = (int) (request.getMinuts() * 1.5);
        usuario.setPunts(usuario.getPunts() + puntosGanados);
        usuario.setAssolimentsTotals(usuario.getAssolimentsTotals() + puntosGanados);

        usuariRepository.save(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Sesión guardada correctamente");
        response.put("puntosGanados", puntosGanados);
        response.put("nuevoSaldo", usuario.getPunts());
        response.put("tareaCompletada", request.getRecordatoriId() != null);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SessioEstudi>> obtenerHistorial(Principal principal) {
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(sessioRepository.findByUsuariId(usuario.getId()));
    }
}
