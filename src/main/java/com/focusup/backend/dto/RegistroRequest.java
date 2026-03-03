package com.focusup.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistroRequest {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Formato de correo electrónico inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$;._*]).{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo ($;._*)"
    )
    private String password;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nom;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String cognoms;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada") 
    private LocalDate dataNaixement;
}