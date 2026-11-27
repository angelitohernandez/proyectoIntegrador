package mx.edu.utez.proyectointegrador.controller;

import mx.edu.utez.proyectointegrador.dto.LoginRequest;
import mx.edu.utez.proyectointegrador.dto.LoginResponse;
import mx.edu.utez.proyectointegrador.dto.UsuarioDto;
import mx.edu.utez.proyectointegrador.model.Usuario;
import mx.edu.utez.proyectointegrador.repository.UsuarioRepository;
import mx.edu.utez.proyectointegrador.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(request.username().trim())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"));
        return new LoginResponse(tokenService.generar(usuario.getId()), UsuarioDto.de(usuario));
    }
}
