package com.focusup.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registro_sesiones")
public class RegistroSesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuari_id", nullable = false)
    private Usuari usuari;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "exito")
    private boolean exito;
}