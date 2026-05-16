package com.focusup.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "grups")
@Data
public class Grup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(name = "codi_acces", unique = true)
    private String codiAcces;

    @OneToMany(mappedBy = "grup")
    private List<GrupUsuari> miembros;
}