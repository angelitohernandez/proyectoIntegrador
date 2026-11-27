package mx.edu.utez.proyectointegrador.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column
    private LocalDate caducidad;

    @Column(nullable = false)
    private Integer descuento;

    @Column(nullable = false)
    private LocalDate creado;

    @Column(name = "foto_version", nullable = false)
    private Long fotoVersion;

    @PrePersist
    void antesDeGuardar() {
        if (creado == null) {
            creado = LocalDate.now();
        }
        if (fotoVersion == null) {
            fotoVersion = 0L;
        }
        if (descuento == null) {
            descuento = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDate getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(LocalDate caducidad) {
        this.caducidad = caducidad;
    }

    public Integer getDescuento() {
        return descuento;
    }

    public void setDescuento(Integer descuento) {
        this.descuento = descuento;
    }

    public LocalDate getCreado() {
        return creado;
    }

    public void setCreado(LocalDate creado) {
        this.creado = creado;
    }

    public Long getFotoVersion() {
        return fotoVersion;
    }

    public void setFotoVersion(Long fotoVersion) {
        this.fotoVersion = fotoVersion;
    }
}
