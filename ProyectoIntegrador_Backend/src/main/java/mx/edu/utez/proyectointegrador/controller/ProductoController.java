package mx.edu.utez.proyectointegrador.controller;

import mx.edu.utez.proyectointegrador.dto.ProductoDto;
import mx.edu.utez.proyectointegrador.dto.ProductoRequest;
import mx.edu.utez.proyectointegrador.model.Rol;
import mx.edu.utez.proyectointegrador.security.SesionService;
import mx.edu.utez.proyectointegrador.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final SesionService sesion;

    public ProductoController(ProductoService productoService, SesionService sesion) {
        this.productoService = productoService;
        this.sesion = sesion;
    }

    @GetMapping
    public List<ProductoDto> listar(@RequestHeader(value = "Authorization", required = false) String auth) {
        sesion.exigir(auth);
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ProductoDto obtener(@RequestHeader(value = "Authorization", required = false) String auth,
                               @PathVariable Long id) {
        sesion.exigir(auth);
        return productoService.obtener(id);
    }

    @GetMapping("/codigo/{codigo}")
    public ProductoDto porCodigo(@RequestHeader(value = "Authorization", required = false) String auth,
                                 @PathVariable String codigo) {
        sesion.exigir(auth);
        return productoService.porCodigo(codigo);
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> foto(@PathVariable Long id) {
        return productoService.foto(id)
                .map(datos -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                        .body(datos))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductoDto crear(@RequestHeader(value = "Authorization", required = false) String auth,
                             @Valid @RequestPart("producto") ProductoRequest request,
                             @RequestPart(value = "foto", required = false) MultipartFile foto) {
        sesion.exigir(auth, Rol.ADMIN, Rol.ENCARGADO);
        return productoService.crear(request, foto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductoDto actualizar(@RequestHeader(value = "Authorization", required = false) String auth,
                                  @PathVariable Long id,
                                  @Valid @RequestPart("producto") ProductoRequest request,
                                  @RequestPart(value = "foto", required = false) MultipartFile foto) {
        sesion.exigir(auth, Rol.ADMIN);
        return productoService.actualizar(id, request, foto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@RequestHeader(value = "Authorization", required = false) String auth,
                                         @PathVariable Long id) {
        sesion.exigir(auth, Rol.ADMIN);
        productoService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
