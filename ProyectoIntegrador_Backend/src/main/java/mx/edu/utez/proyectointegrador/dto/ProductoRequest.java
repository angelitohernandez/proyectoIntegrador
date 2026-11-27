package mx.edu.utez.proyectointegrador.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProductoRequest(
        @NotBlank @Size(max = 60) String codigo,
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 80) String categoria,
        @NotNull @DecimalMin("0.0") Double precio,
        @NotNull @Min(0) Integer stock,
        @NotNull @Min(0) Integer stockMinimo,
        LocalDate caducidad,
        @NotNull @Min(0) @Max(100) Integer descuento) {
}
