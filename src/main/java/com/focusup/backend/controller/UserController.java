package com.focusup.backend.controller;

import com.focusup.backend.dto.ActualitzarPerfilRequest;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.UsuariRepository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // NUEVO: Necesario para la contraseña

    // 1. VER PERFIL
    @GetMapping("/me")
    public ResponseEntity<?> obtenerPerfil() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuari usuari = usuariRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(usuari);
    }

    // 2. ACTUALIZAR NOMBRE Y APELLIDOS (El que ya tenías)
    @PutMapping("/me")
    public ResponseEntity<?> actualizarPerfil(@RequestBody ActualitzarPerfilRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuari usuari = usuariRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getNom() != null) usuari.setNom(request.getNom());
        if (request.getCognoms() != null) usuari.setCognoms(request.getCognoms());

        Usuari usuarioActualizado = usuariRepository.save(usuari);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // 3. ACTUALIZAR EMAIL (NUEVO)
    @PatchMapping("/me/email")
    public ResponseEntity<?> actualizarEmail(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuari usuari = usuariRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevoEmail = request.get("email");

        // Comprobamos que el email no esté ya siendo usado por otra persona
        boolean usuarioExistente = usuariRepository.existsByEmail(nuevoEmail);
        if (usuarioExistente == true) {
            return ResponseEntity.badRequest().body(Map.of("error", "Este correo electrónico ya está en uso."));
        }

        usuari.setEmail(nuevoEmail);
        usuariRepository.save(usuari);

        return ResponseEntity.ok(Map.of("mensaje", "Correo electrónico actualizado correctamente."));
    }

    // 4. ACTUALIZAR CONTRASEÑA (NUEVO)
    @PatchMapping("/me/password")
    public ResponseEntity<?> actualizarPassword(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuari usuari = usuariRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String passwordActual = request.get("passwordActual");
        String passwordNueva = request.get("passwordNueva");

        // 1º Barrera de seguridad: Comprobar que sabe su contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuari.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña actual es incorrecta."));
        }

        // 2º Barrera: Encriptamos la nueva y la guardamos
        usuari.setPassword(passwordEncoder.encode(passwordNueva));
        usuariRepository.save(usuari);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada de forma segura."));
    }
}
