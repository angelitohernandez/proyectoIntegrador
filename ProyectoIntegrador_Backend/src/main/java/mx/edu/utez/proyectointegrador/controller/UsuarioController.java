package mx.edu.utez.proyectointegrador.controller;

import mx.edu.utez.proyectointegrador.dto.UsuarioDto;
import mx.edu.utez.proyectointegrador.dto.UsuarioRequest;
import mx.edu.utez.proyectointegrador.model.Rol;
import mx.edu.utez.proyectointegrador.model.Usuario;
import mx.edu.utez.proyectointegrador.security.SesionService;
import mx.edu.utez.proyectointegrador.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SesionService sesion;

    public UsuarioController(UsuarioService usuarioService, SesionService sesion) {
        this.usuarioService = usuarioService;
        this.sesion = sesion;
    }

    @GetMapping
    public List<UsuarioDto> listar(@RequestHeader(value = "Authorization", required = false) String auth) {
        sesion.exigir(auth, Rol.ADMIN);
        return usuarioService.listar();
    }

    @PostMapping
    public UsuarioDto crear(@RequestHeader(value = "Authorization", required = false) String auth,
                            @Valid @RequestBody UsuarioRequest request) {
        sesion.exigir(auth, Rol.ADMIN);
        return usuarioService.crear(request);
    }

    @PutMapping("/{id}")
    public UsuarioDto actualizar(@RequestHeader(value = "Authorization", required = false) String auth,
                                 @PathVariable Long id,
                                 @Valid @RequestBody UsuarioRequest request) {
        sesion.exigir(auth, Rol.ADMIN);
        return usuarioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@RequestHeader(value = "Authorization", required = false) String auth,
                                         @PathVariable Long id) {
        Usuario actual = sesion.exigir(auth, Rol.ADMIN);
        usuarioService.eliminar(id, actual.getId());
        return ResponseEntity.ok().build();
    }
}
