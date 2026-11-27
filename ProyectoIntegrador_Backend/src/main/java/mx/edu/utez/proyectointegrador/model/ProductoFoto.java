package mx.edu.utez.proyectointegrador.model;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_fotos")
public class ProductoFoto {

    @Id
    @Column(name = "producto_id")
    private Long productoId;

    @Lob
    @Column(nullable = false)
    private byte[] datos;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
    }
}
