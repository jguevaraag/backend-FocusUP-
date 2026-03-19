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
    
    private boolean completat = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuari_id", nullable = false)
    @JsonIgnore
    private Usuari usuari;
}
