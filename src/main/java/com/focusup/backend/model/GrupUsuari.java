package com.focusup.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "grup_usuaris")
@Data
public class GrupUsuari {

    @EmbeddedId
    private GrupUsuariID id = new GrupUsuariID();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("grupId") // Conecta con la clave compuesta
    @JoinColumn(name = "grup_id")
    private Grup grup;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuariId") // Conecta con la clave compuesta
    @JoinColumn(name = "usuari_id")
    private Usuari usuari;

    @Column(name = "data_unio")
    private LocalDateTime dataUnio;
}