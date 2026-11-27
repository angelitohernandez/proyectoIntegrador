package mx.edu.utez.proyectointegrador.controller;

import mx.edu.utez.proyectointegrador.dto.MermaDto;
import mx.edu.utez.proyectointegrador.dto.MermaRequest;
import mx.edu.utez.proyectointegrador.model.Rol;
import mx.edu.utez.proyectointegrador.model.Usuario;
import mx.edu.utez.proyectointegrador.security.SesionService;
import mx.edu.utez.proyectointegrador.service.MermaService;
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
@RequestMapping("/api/mermas")
public class MermaController {

    private final MermaService mermaService;
    private final SesionService sesion;

    public MermaController(MermaService mermaService, SesionService sesion) {
        this.mermaService = mermaService;
        this.sesion = sesion;
    }

    @GetMapping
    public List<MermaDto> listar(@RequestHeader(value = "Authorization", required = false) String auth) {
        sesion.exigir(auth);
        return mermaService.listar();
    }

    @PostMapping
    public MermaDto crear(@RequestHeader(value = "Authorization", required = false) String auth,
                          @Valid @RequestBody MermaRequest request) {
        Usuario usuario = sesion.exigir(auth, Rol.ADMIN);
        return mermaService.crear(request, usuario);
    }

    @PutMapping("/{id}")
    public MermaDto actualizar(@RequestHeader(value = "Authorization", required = false) String auth,
                               @PathVariable Long id,
                               @Valid @RequestBody MermaRequest request) {
        sesion.exigir(auth, Rol.ADMIN);
        return mermaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@RequestHeader(value = "Authorization", required = false) String auth,
                                         @PathVariable Long id) {
        sesion.exigir(auth, Rol.ADMIN);
        mermaService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
