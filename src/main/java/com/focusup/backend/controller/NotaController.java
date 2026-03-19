package com.focusup.backend.controller;

import com.focusup.backend.dto.NotaRequest;
import com.focusup.backend.model.Nota;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.NotaRepository;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @GetMapping
    public ResponseEntity<List<Nota>> obtenerMisNotas(Principal principal) {
        // Buscamos al usuario que está haciendo la petición.
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Devolvemos solo SUS notas.
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
        // Buscamos al usuario dueño del token.
        Usuari usuario = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Creamos la nota
        Nota nuevaNota = new Nota();
        nuevaNota.setTitol(request.getTitol());
        nuevaNota.setContingut(request.getContingut());
        
        // Si nos envían fecha, la usamos. Si no, ponemos la de hoy.
        if (request.getData() != null) {
            nuevaNota.setData(request.getData());
        } else {
            nuevaNota.setData(LocalDate.now());
        }

        // Asignamos el usuario a la nota.
        nuevaNota.setUsuari(usuario);

        notaRepository.save(nuevaNota);

        return ResponseEntity.ok("Nota guardada correctamente");
    }
}
