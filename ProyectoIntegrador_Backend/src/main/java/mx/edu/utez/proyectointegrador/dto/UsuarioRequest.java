package mx.edu.utez.proyectointegrador.dto;

import mx.edu.utez.proyectointegrador.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 100) String nombre,
        String password,
        @NotNull Rol rol) {
}
