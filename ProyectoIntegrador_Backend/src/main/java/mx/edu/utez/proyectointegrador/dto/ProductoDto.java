package mx.edu.utez.proyectointegrador.dto;

import mx.edu.utez.proyectointegrador.model.Producto;

import java.time.LocalDate;

public record ProductoDto(
        Long id,
        String codigo,
        String nombre,
        String categoria,
        Double precio,
        Integer stock,
        Integer stockMinimo,
        LocalDate caducidad,
        Integer descuento,
        LocalDate creado,
        Long fotoVersion) {

    public static ProductoDto de(Producto p) {
        return new ProductoDto(p.getId(), p.getCodigo(), p.getNombre(), p.getCategoria(), p.getPrecio(),
                p.getStock(), p.getStockMinimo(), p.getCaducidad(), p.getDescuento(), p.getCreado(),
                p.getFotoVersion());
    }
}
