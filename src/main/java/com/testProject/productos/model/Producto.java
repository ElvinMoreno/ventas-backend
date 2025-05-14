package com.testProject.productos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "productos", 
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"nombre", "categoria_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stockTotal = 0;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoColor> colores = new ArrayList<>();

    public void actualizarStockTotal() {
        this.stockTotal = colores.stream()
                .mapToInt(ProductoColor::getStockColor)
                .sum();
    }
}