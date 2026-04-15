package com.focusup.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class GrupUsuariID implements Serializable {
    @Column(name = "grup_id")
    private Long grupId;

    @Column(name = "usuari_id")
    private Long usuariId;
}
