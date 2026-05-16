package com.focusup.backend.controller;

import com.focusup.backend.dto.NotaRequest;
import com.focusup.backend.model.Nota;
import com.focusup.backend.model.SessioEstudi;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.NotaRepository;
import com.focusup.backend.repository.SessioEstudiRepository;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private SessioEstudiRepository sessioRepository; 


    @GetMapping
    public ResponseEntity<List<Nota>> obtenerMisNotas(Principal principal) {
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<Nota> notas = notaRepository.findByUsuariId(usuario.getId());
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/date/{fecha}")
    public ResponseEntity<List<Nota>> obtenerNotasPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Principal principal) {
        
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<Nota> notasDelDia = notaRepository.findByDataAndUsuariId(fecha, usuario.getId());
        
        return ResponseEntity.ok(notasDelDia);
    }

    @PostMapping
    public ResponseEntity<?> crearNota(@RequestBody NotaRequest request, Principal principal) {
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Nota nuevaNota = new Nota();
        nuevaNota.setTitol(request.getTitol());
        nuevaNota.setContingut(request.getContingut());
        
        if (request.getData() != null) {
            nuevaNota.setData(request.getData());
        } else {
            nuevaNota.setData(LocalDate.now());
        }

        nuevaNota.setUsuari(usuario);

        notaRepository.save(nuevaNota);

        return ResponseEntity.ok("Nota guardada correctamente");
    }

    @PostMapping("/session/{sessionId}")
    public ResponseEntity<?> crearNotaDiario(@PathVariable Long sessionId, @RequestBody Map<String, String> request, Principal principal) {
        
      
        Usuari usuari = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        String contenido = request.get("contingut");
        String titulo = request.get("titol");

        SessioEstudi sessio = sessioRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        if (!sessio.getUsuari().getId().equals(usuari.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para comentar esta sesión"));
        }

      
        if (notaRepository.existsBySessioEstudiId(sessionId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ya has escrito un resumen para esta sesión"));
        }

        
        Nota nota = new Nota();
        nota.setTitol(titulo != null ? titulo : "Resumen de sesión " + sessio.getId());
        nota.setContingut(contenido);
        nota.setUsuari(usuari);
        nota.setSessioEstudi(sessio);
        notaRepository.save(nota);

       
        int recompensa = 15;
        usuari.setPunts(usuari.getPunts() + recompensa);
        usuariRepository.save(usuari);

        return ResponseEntity.ok(Map.of(
            "mensaje", "¡Nota guardada! Has ganado " + recompensa + " monedas extra por tu resumen.",
            "nuevos_puntos", usuari.getPunts()
        ));
    }
}
