package com.focusup.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.focusup.backend.security.JwtService;
import com.focusup.backend.dto.LoginRequest;
import com.focusup.backend.dto.RegistroRequest;
import com.focusup.backend.model.Role;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.UsuariRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; //Comprobador de credenciales.

    @Autowired
    private JwtService jwtService;

        @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequest request) {

        // Comprobamos si el usuario ya existe.
        if (usuariRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Error: El nombre de usuario ya está en uso");
        }

        if (usuariRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado");
        }

        // Creamos el Usuario.
        Usuari nuevoUsuario = new Usuari();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setNom(request.getNom());
        nuevoUsuario.setCognoms(request.getCognoms());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setDataNaixement(request.getDataNaixement());

        // Encriptamos la contraseña antes de guardar.
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(Role.USER);

        // Guardamos en la Base de Datos.
        usuariRepository.save(nuevoUsuario);

        return ResponseEntity.ok("¡Usuario registrado con éxito!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Comprueba usuario y contraseñas.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        
        if (authentication.isAuthenticated()) {
            // Generamos el Token JWT.
            String token = jwtService.generateToken(request.getUsername());
            return ResponseEntity.ok(token); // Devolvemos el "sello"
        } else {
            return ResponseEntity.badRequest().body("Credenciales incorrectas");
        }
    }
}
