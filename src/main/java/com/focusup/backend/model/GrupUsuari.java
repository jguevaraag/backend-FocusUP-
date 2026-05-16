package com.focusup.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "grup_usuaris")
@Data
public class GrupUsuari {

    @EmbeddedId
    private GrupUsuariID id = new GrupUsuariID();

    @ToString.Exclude   
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("grupId")
    @JoinColumn(name = "grup_id")
    private Grup grup;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("usuariId")
    @JoinColumn(name = "usuari_id")
    private Usuari usuari;

    @Column(name = "data_unio")
    private LocalDateTime dataUnio;
}