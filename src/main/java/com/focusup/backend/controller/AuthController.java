package com.focusup.backend.controller;

import java.time.LocalDateTime;
import java.util.Map;

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
import com.focusup.backend.model.RegistroSesion;
import com.focusup.backend.model.Role;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.RegistroSesionRepository;
import com.focusup.backend.repository.UsuariRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RegistroSesionRepository registroSesionRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequest request) {

        if (usuariRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Error: El nombre de usuario ya está en uso");
        }

        if (usuariRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado");
        }

        Usuari nuevoUsuario = new Usuari();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setNom(request.getNom());
        nuevoUsuario.setCognoms(request.getCognoms());
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setDataNaixement(request.getDataNaixement());

        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(Role.USER);

        usuariRepository.save(nuevoUsuario);

        return ResponseEntity.ok("¡Usuario registrado con éxito!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Map<String, String> requestPayload, HttpServletRequest request) {
        
        String username = requestPayload.get("username");
        String password = requestPayload.get("password");
        
        String ipAddress = request.getRemoteAddr();

        Usuari usuario = usuariRepository.findByUsername(username).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }

        if (usuario.isBloqueado()) {
            
            registroSesionRepository.save(new RegistroSesion(null,usuario, ipAddress, LocalDateTime.now(), false));
            return ResponseEntity.status(403).body(Map.of("error", "Cuenta bloqueada."));
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            int intentos = usuario.getIntentosFallidos() + 1;
            usuario.setIntentosFallidos(intentos);

            if (intentos >= 3) {
                usuario.setBloqueado(true);
            }
            usuariRepository.save(usuario);
            
            
            registroSesionRepository.save(new RegistroSesion(null, usuario, ipAddress, LocalDateTime.now(), false));
            
            return ResponseEntity.status(401).body(Map.of(
                "error", "Contraseña incorrecta. Te quedan " + (3 - intentos) + " intentos."
            ));
        }

        if (usuario.getIntentosFallidos() > 0) {
            usuario.setIntentosFallidos(0);
            usuariRepository.save(usuario);
        }

        registroSesionRepository.save(new RegistroSesion(null, usuario, ipAddress, LocalDateTime.now(), true));

        String token = jwtService.generateToken(usuario.getUsername());

        return ResponseEntity.ok(Map.of(
                "mensaje", "Login exitoso",
                "token", token,
                "rol", usuario.getRol().name()
        ));
    }
}
