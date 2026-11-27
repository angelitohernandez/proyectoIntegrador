package mx.edu.utez.proyectointegrador.service;

import mx.edu.utez.proyectointegrador.dto.MermaDto;
import mx.edu.utez.proyectointegrador.dto.MermaRequest;
import mx.edu.utez.proyectointegrador.model.Merma;
import mx.edu.utez.proyectointegrador.model.Producto;
import mx.edu.utez.proyectointegrador.model.Usuario;
import mx.edu.utez.proyectointegrador.repository.MermaRepository;
import mx.edu.utez.proyectointegrador.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MermaService {

    private final MermaRepository mermaRepository;
    private final ProductoRepository productoRepository;

    public MermaService(MermaRepository mermaRepository, ProductoRepository productoRepository) {
        this.mermaRepository = mermaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<MermaDto> listar() {
        return mermaRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha", "id")).stream()
                .map(MermaDto::de).toList();
    }

    @Transactional
    public MermaDto crear(MermaRequest request, Usuario usuario) {
        Producto producto = buscarProducto(request.productoId());
        descontar(producto, request.cantidad());
        Merma merma = new Merma();
        merma.setProducto(producto);
        merma.setRegistradoPor(usuario.getNombre());
        aplicar(merma, request);
        return MermaDto.de(mermaRepository.save(merma));
    }

    @Transactional
    public MermaDto actualizar(Long id, MermaRequest request) {
        Merma merma = buscar(id);
        Producto anterior = merma.getProducto();
        anterior.setStock(anterior.getStock() + merma.getCantidad());
        Producto nuevo = buscarProducto(request.productoId());
        descontar(nuevo, request.cantidad());
        merma.setProducto(nuevo);
        aplicar(merma, request);
        return MermaDto.de(mermaRepository.save(merma));
    }

    @Transactional
    public void eliminar(Long id) {
        Merma merma = buscar(id);
        Producto producto = merma.getProducto();
        producto.setStock(producto.getStock() + merma.getCantidad());
        mermaRepository.delete(merma);
    }

    private Merma buscar(Long id) {
        return mermaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de merma no encontrado"));
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private void descontar(Producto producto, int cantidad) {
        if (cantidad > producto.getStock()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La merma excede el stock disponible (" + producto.getStock() + ")");
        }
        producto.setStock(producto.getStock() - cantidad);
    }

    private void aplicar(Merma merma, MermaRequest request) {
        merma.setCantidad(request.cantidad());
        merma.setMotivo(request.motivo().trim());
        merma.setFecha(request.fecha());
        String obs = request.observaciones();
        merma.setObservaciones(obs == null || obs.isBlank() ? null : obs.trim());
    }
}
