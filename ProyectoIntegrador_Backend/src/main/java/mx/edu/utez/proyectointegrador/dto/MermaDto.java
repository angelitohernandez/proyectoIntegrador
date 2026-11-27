package mx.edu.utez.proyectointegrador.dto;

import mx.edu.utez.proyectointegrador.model.Merma;

import java.time.LocalDate;

public record MermaDto(
        Long id,
        Long productoId,
        String productoNombre,
        Double productoPrecio,
        Integer cantidad,
        String motivo,
        LocalDate fecha,
        String observaciones,
        String registradoPor) {

    public static MermaDto de(Merma m) {
        return new MermaDto(m.getId(), m.getProducto().getId(), m.getProducto().getNombre(),
                m.getProducto().getPrecio(), m.getCantidad(), m.getMotivo(), m.getFecha(),
                m.getObservaciones(), m.getRegistradoPor());
    }
}
