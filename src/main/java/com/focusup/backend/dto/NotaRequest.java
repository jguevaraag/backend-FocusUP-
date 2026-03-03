package com.focusup.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class NotaRequest {
    private String titol;
    private String contingut;
    private LocalDate data;
}
