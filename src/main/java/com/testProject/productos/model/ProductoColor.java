package com.testProject.productos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto_colores")
@Data
@NoArgsConstructor
public class ProductoColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @Column(nullable = false)
    private Integer stockColor = 0; // Inicialización por defecto

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @OneToMany(mappedBy = "productoColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoTalla> tallas = new ArrayList<>();

    public ProductoColor(Producto producto, Color color) {
        this.producto = producto;
        this.color = color;
        this.stockColor = 0;
    }

    public void actualizarStockColor() {
        this.stockColor = (tallas != null) ? 
            tallas.stream().mapToInt(ProductoTalla::getStock).sum() : 
            0;
    }
}