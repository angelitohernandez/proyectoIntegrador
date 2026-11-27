package mx.edu.utez.proyectointegrador.service;

import mx.edu.utez.proyectointegrador.dto.ProductoDto;
import mx.edu.utez.proyectointegrador.dto.ProductoRequest;
import mx.edu.utez.proyectointegrador.model.Producto;
import mx.edu.utez.proyectointegrador.model.ProductoFoto;
import mx.edu.utez.proyectointegrador.repository.MermaRepository;
import mx.edu.utez.proyectointegrador.repository.ProductoFotoRepository;
import mx.edu.utez.proyectointegrador.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoFotoRepository fotoRepository;
    private final MermaRepository mermaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           ProductoFotoRepository fotoRepository,
                           MermaRepository mermaRepository) {
        this.productoRepository = productoRepository;
        this.fotoRepository = fotoRepository;
        this.mermaRepository = mermaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> listar() {
        return productoRepository.findAll(Sort.by("nombre")).stream().map(ProductoDto::de).toList();
    }

    @Transactional(readOnly = true)
    public ProductoDto obtener(Long id) {
        return ProductoDto.de(buscar(id));
    }

    @Transactional(readOnly = true)
    public ProductoDto porCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .map(ProductoDto::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Transactional
    public ProductoDto crear(ProductoRequest request, MultipartFile foto) {
        String codigo = request.codigo().trim();
        if (productoRepository.findByCodigo(codigo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
        }
        Producto producto = new Producto();
        aplicar(producto, request);
        producto = productoRepository.save(producto);
        guardarFoto(producto, foto);
        return ProductoDto.de(producto);
    }

    @Transactional
    public ProductoDto actualizar(Long id, ProductoRequest request, MultipartFile foto) {
        Producto producto = buscar(id);
        String codigo = request.codigo().trim();
        Optional<Producto> existente = productoRepository.findByCodigo(codigo);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
        }
        aplicar(producto, request);
        producto = productoRepository.save(producto);
        guardarFoto(producto, foto);
        return ProductoDto.de(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscar(id);
        mermaRepository.deleteByProductoId(id);
        fotoRepository.deleteById(id);
        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public Optional<byte[]> foto(Long id) {
        return fotoRepository.findById(id).map(ProductoFoto::getDatos);
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private void aplicar(Producto producto, ProductoRequest request) {
        producto.setCodigo(request.codigo().trim());
        producto.setNombre(request.nombre().trim());
        producto.setCategoria(request.categoria().trim());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setStockMinimo(request.stockMinimo());
        producto.setCaducidad(request.caducidad());
        producto.setDescuento(request.descuento());
    }

    private void guardarFoto(Producto producto, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return;
        }
        try {
            ProductoFoto foto = fotoRepository.findById(producto.getId()).orElseGet(ProductoFoto::new);
            foto.setProductoId(producto.getId());
            foto.setDatos(archivo.getBytes());
            fotoRepository.save(foto);
            producto.setFotoVersion(System.currentTimeMillis());
            productoRepository.save(producto);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer la imagen");
        }
    }
}
