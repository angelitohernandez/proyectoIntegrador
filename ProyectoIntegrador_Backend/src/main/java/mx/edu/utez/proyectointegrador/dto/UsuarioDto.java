package mx.edu.utez.proyectointegrador.dto;

import mx.edu.utez.proyectointegrador.model.Rol;
import mx.edu.utez.proyectointegrador.model.Usuario;

public record UsuarioDto(Long id, String username, String nombre, Rol rol) {

    public static UsuarioDto de(Usuario u) {
        return new UsuarioDto(u.getId(), u.getUsername(), u.getNombre(), u.getRol());
    }
}
