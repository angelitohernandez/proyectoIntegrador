package mx.edu.utez.proyectointegrador.security;

import mx.edu.utez.proyectointegrador.model.Rol;
import mx.edu.utez.proyectointegrador.model.Usuario;
import mx.edu.utez.proyectointegrador.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Service
public class SesionService {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SesionService(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario exigir(String authorization, Rol... permitidos) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida");
        }
        Long id = tokenService.validar(authorization.substring(7));
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tu sesión expiró. Inicia sesión de nuevo.");
        }
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        if (permitidos.length > 0 && !Arrays.asList(permitidos).contains(usuario.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción");
        }
        return usuario;
    }
}
