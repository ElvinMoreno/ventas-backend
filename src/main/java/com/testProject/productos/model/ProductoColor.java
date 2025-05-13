package com.testProject.productos.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto_colores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @Column(nullable = false)
    private Integer stockColor; 
    
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @OneToMany(mappedBy = "productoColor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoTalla> tallas;
    

    public void actualizarStockColor() {
        this.stockColor = tallas.stream()
                .mapToInt(ProductoTalla::getStock)
                .sum();
        producto.actualizarStockTotal();
    }
}