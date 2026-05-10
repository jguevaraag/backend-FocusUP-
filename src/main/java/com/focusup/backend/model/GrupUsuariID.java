package com.focusup.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Embeddable
@Data
public class GrupUsuariID implements Serializable {

    @JsonIgnore
    @Column(name = "grup_id")
    private Long grupId;

    @Column(name = "usuari_id")
    private Long usuariId;
}
