package mx.edu.utez.proyectointegrador.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MermaRequest(
        @NotNull Long productoId,
        @NotNull @Min(1) Integer cantidad,
        @NotBlank @Size(max = 30) String motivo,
        @NotNull LocalDate fecha,
        @Size(max = 300) String observaciones) {
}
