package com.focusup.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatsDTO {
    private int totalMinutosEstudiados;
    private long tareasCompletadas;
    private int rachaActual;
    private int puntosActuales;
}
