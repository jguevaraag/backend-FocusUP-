package com.focusup.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecordatoriRequest {
    private String missatge;
    private LocalDateTime dataHora; 
}
