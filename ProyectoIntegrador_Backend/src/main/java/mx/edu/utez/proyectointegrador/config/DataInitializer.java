package mx.edu.utez.proyectointegrador.config;

import mx.edu.utez.proyectointegrador.model.Merma;
import mx.edu.utez.proyectointegrador.model.Producto;
import mx.edu.utez.proyectointegrador.model.Rol;
import mx.edu.utez.proyectointegrador.model.Usuario;
import mx.edu.utez.proyectointegrador.repository.MermaRepository;
import mx.edu.utez.proyectointegrador.repository.ProductoRepository;
import mx.edu.utez.proyectointegrador.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final MermaRepository mermaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, ProductoRepository productoRepository,
                           MermaRepository mermaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.mermaRepository = mermaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            crearUsuario("admin", "Administrador General", "admin123", Rol.ADMIN);
            crearUsuario("encargado", "Elena Encargada", "encargado123", Rol.ENCARGADO);
            crearUsuario("vendedor", "Victor Vendedor", "vendedor123", Rol.VENDEDOR);
        }
        if (productoRepository.count() == 0) {
            LocalDate hoy = LocalDate.now();
            Producto leche = crearProducto("7501001000011", "Leche entera 1L", "Lácteos", 28.5, 40, 10, hoy.plusDays(3), 0);
            crearProducto("7501001000028", "Yogurt natural 500g", "Lácteos", 35.0, 18, 8, hoy.plusDays(6), 15);
            Producto pan = crearProducto("7501001000035", "Pan de caja integral", "Panadería", 42.0, 12, 6, hoy.plusDays(2), 20);
            crearProducto("7501001000042", "Manzana roja kg", "Frutas y verduras", 48.0, 25, 10, hoy.plusDays(9), 0);
            crearProducto("7501001000059", "Pechuga de pollo kg", "Carnes", 119.0, 4, 8, hoy.plusDays(4), 0);
            crearProducto("7501001000066", "Refresco cola 600ml", "Bebidas", 19.0, 80, 20, hoy.plusDays(120), 10);
            crearProducto("7501001000073", "Arroz blanco 1kg", "Abarrotes", 27.0, 55, 15, hoy.plusDays(300), 0);
            crearProducto("7501001000080", "Detergente líquido 1L", "Limpieza", 62.0, 3, 6, null, 0);

            Usuario admin = usuarioRepository.findByUsernameIgnoreCase("admin").orElse(null);
            crearMerma(leche, 2, "Caducado", hoy.minusDays(1), "Empaque inflado", admin);
            crearMerma(pan, 3, "Dañado", hoy.minusDays(2), "Aplastado en transporte", admin);
        }
    }

    private void crearUsuario(String username, String nombre, String password, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNombre(nombre);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    private Producto crearProducto(String codigo, String nombre, String categoria, double precio,
                                   int stock, int minimo, LocalDate caducidad, int descuento) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setStockMinimo(minimo);
        producto.setCaducidad(caducidad);
        producto.setDescuento(descuento);
        return productoRepository.save(producto);
    }

    private void crearMerma(Producto producto, int cantidad, String motivo, LocalDate fecha,
                            String observaciones, Usuario usuario) {
        Merma merma = new Merma();
        merma.setProducto(producto);
        merma.setCantidad(cantidad);
        merma.setMotivo(motivo);
        merma.setFecha(fecha);
        merma.setObservaciones(observaciones);
        merma.setRegistradoPor(usuario != null ? usuario.getNombre() : null);
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
        mermaRepository.save(merma);
    }
}
