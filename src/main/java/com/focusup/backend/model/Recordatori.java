package com.focusup.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recordatoris")
@Data
public class Recordatori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String missatge;
    
    @Column(name = "data_hora")
    private LocalDateTime dataHora; 
    
    @Column(name = "completat")
    private boolean completat = false;

    @Column(name = "penalitzat")
    private boolean penalitzat = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuari_id", nullable = false)
    @JsonIgnore
    private Usuari usuari;

    @ManyToOne
    @JoinColumn(name = "grup_id")
    @JsonIgnore
    private Grup grup;
    
}
