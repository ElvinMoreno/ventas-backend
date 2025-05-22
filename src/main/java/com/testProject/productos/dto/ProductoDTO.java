package com.testProject.productos.dto;

import com.testProject.productos.model.Producto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;

    public ProductoDTO(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.categoria = producto.getCategoria() != null ? producto.getCategoria().getNombre() : null;
    }
}