package com.focusup.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuaris")

public class Usuari implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; 

    private String nom;
    private String cognoms;

    @Column(name = "data_naixement")
    private LocalDate dataNaixement;

    private Integer punts = 0;
    private Integer assolimentsTotals = 0;

    @Enumerated(EnumType.STRING) 
    private Role rol; 

    @Column(name = "data_ultima_ruleta")
    private LocalDate dataUltimaRuleta;

    @Column(name = "racha_actual")
    private Integer rachaActual = 0;

    @Column(name = "ultima_connexio")
    private LocalDate ultimaConnexio;

    @Column(name = "intentos_fallidos")
    private int intentosFallidos = 0;

    @Column(name = "bloqueado")
    private boolean bloqueado = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
