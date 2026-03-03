package com.focusup.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notes")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titol;

    @Column(columnDefinition = "TEXT") 
    private String contingut;

    
    @Column(name = "data") 
    private LocalDate data; 

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuari_id", nullable = false)
    private Usuari usuari;
}
