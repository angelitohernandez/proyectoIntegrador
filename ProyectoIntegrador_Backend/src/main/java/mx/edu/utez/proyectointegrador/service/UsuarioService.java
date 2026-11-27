package mx.edu.utez.proyectointegrador.service;

import mx.edu.utez.proyectointegrador.dto.UsuarioDto;
import mx.edu.utez.proyectointegrador.dto.UsuarioRequest;
import mx.edu.utez.proyectointegrador.model.Usuario;
import mx.edu.utez.proyectointegrador.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listar() {
        return usuarioRepository.findAll(Sort.by("nombre")).stream().map(UsuarioDto::de).toList();
    }

    @Transactional
    public UsuarioDto crear(UsuarioRequest request) {
        String username = request.username().trim().toLowerCase();
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese nombre de usuario ya existe");
        }
        if (request.password() == null || request.password().length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 4 caracteres");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNombre(request.nombre().trim());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setRol(request.rol());
        return UsuarioDto.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDto actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        String username = request.username().trim().toLowerCase();
        Optional<Usuario> existente = usuarioRepository.findByUsernameIgnoreCase(username);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese nombre de usuario ya existe");
        }
        usuario.setUsername(username);
        usuario.setNombre(request.nombre().trim());
        usuario.setRol(request.rol());
        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 4) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 4 caracteres");
            }
            usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return UsuarioDto.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id, Long usuarioActualId) {
        if (id.equals(usuarioActualId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes eliminar tu propio usuario");
        }
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
