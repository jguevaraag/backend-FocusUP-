package com.focusup.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
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

import com.focusup.backend.model.SessioEstudi;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.SessioEstudiRepository;
import com.focusup.backend.repository.UsuariRepository;

@RestController
@RequestMapping("/api/sessions")
public class SessioEstudiController {

        @Autowired
    private SessioEstudiRepository sessioRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @PostMapping
    public ResponseEntity<?> guardarSesion(@RequestBody Map<String, Integer> request, Principal principal) {
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Leemos "minuts" del JSON.
        Integer minutos = request.get("minuts");
        
        if (minutos == null || minutos <= 0) {
            return ResponseEntity.badRequest().body("Los minutos deben ser mayor a 0");
        }

        SessioEstudi sesion = new SessioEstudi();
        sesion.setDuracioMinuts(minutos);
        
        // Calculo de la fecha de inicio y fin de la sesión.
        LocalDateTime ahora = LocalDateTime.now();
        sesion.setDataFi(ahora); // Terminó ahora
        sesion.setDataInici(ahora.minusMinutes(minutos)); // Empezó hace X minutos.
        
        sesion.setUsuari(usuario);
        
        sessioRepository.save(sesion);

        // Calculamos los puntos ganados (1 punto por minuto) y actualizamos el usuario.
        int puntosGanados = minutos; 
        usuario.setPunts(usuario.getPunts() + puntosGanados);
        usuario.setAssolimentsTotals(usuario.getAssolimentsTotals() + puntosGanados);
        
        usuariRepository.save(usuario);

        return ResponseEntity.ok("Sesión guardada. ¡Has ganado " + puntosGanados + " puntos!");
    }

    @GetMapping
    public ResponseEntity<List<SessioEstudi>> obtenerHistorial(Principal principal) {
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(sessioRepository.findByUsuariId(usuario.getId()));
    }
}
