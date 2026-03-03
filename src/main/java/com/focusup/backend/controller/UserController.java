package com.focusup.backend.controller;

import com.focusup.backend.dto.ActualitzarPerfilRequest;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UsuariRepository usuariRepository;

    @GetMapping("/me")
    public ResponseEntity<?> obtenerPerfil() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuari usuari = usuariRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(usuari);
    }

    @PutMapping("/me")
    public ResponseEntity<?> actualizarPerfil(@RequestBody ActualitzarPerfilRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuari usuari = usuariRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizamos los campos permitidos.
        usuari.setNom(request.getNom());
        usuari.setCognoms(request.getCognoms());

        // Guardamos los cambios en la base de datos.
        Usuari usuarioActualizado = usuariRepository.save(usuari);

        return ResponseEntity.ok(usuarioActualizado);
    }


}
