package com.focusup.backend.controller;

import com.focusup.backend.dto.RecordatoriRequest;
import com.focusup.backend.model.Recordatori;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.RecordatoriRepository;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/recordatoris")
public class RecordatoriController {

    @Autowired
    private RecordatoriRepository recordatoriRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    private Usuari getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuariRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    
    @PostMapping
    public ResponseEntity<Recordatori> crearRecordatori(@RequestBody RecordatoriRequest request) {
        Usuari usuari = getUsuarioAutenticado();
        
        Recordatori recordatori = new Recordatori();
        recordatori.setMissatge(request.getMissatge());
        recordatori.setDataHora(request.getDataHora());
        recordatori.setUsuari(usuari);
        
        return ResponseEntity.ok(recordatoriRepository.save(recordatori));
    }

    @GetMapping
    public ResponseEntity<List<Recordatori>> getMisRecordatoris() {
        Usuari usuari = getUsuarioAutenticado();
        return ResponseEntity.ok(recordatoriRepository.findByUsuari(usuari));
    }

    @GetMapping("/date/{fecha}")
    public ResponseEntity<List<Recordatori>> getRecordatorisPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        Usuari usuari = getUsuarioAutenticado();
        
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
        
        return ResponseEntity.ok(recordatoriRepository.findByUsuariAndDataHoraBetween(usuari, inicioDia, finDia));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Recordatori> toggleCompletado(@PathVariable Long id) {
        Usuari usuari = getUsuarioAutenticado();
        Recordatori recordatori = recordatoriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));
                
        if (!recordatori.getUsuari().getId().equals(usuari.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        recordatori.setCompletat(!recordatori.isCompletat());
        return ResponseEntity.ok(recordatoriRepository.save(recordatori));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarRecordatori(@PathVariable Long id) {
        Usuari usuari = getUsuarioAutenticado();
        Recordatori recordatori = recordatoriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));
                
        if (!recordatori.getUsuari().getId().equals(usuari.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        recordatoriRepository.delete(recordatori);
        return ResponseEntity.ok("Borrado con éxito");
    }
}
