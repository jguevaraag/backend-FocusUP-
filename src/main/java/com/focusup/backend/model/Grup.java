package com.focusup.backend.model;

import jakarta.persistence.*;
import lombok.Data;

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
}