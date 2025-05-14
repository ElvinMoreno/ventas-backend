package com.testProject.productos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoResponseDTO {
    private String nombre;
    private Double precio;
    private String categoria;
    private String color;
    private String talla;
    private Integer stock;
    
    // Constructor
    public ProductoResponseDTO(String nombre, Double precio, String categoria, 
                             String color, String talla, Integer stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.color = color;
        this.talla = talla;
        this.stock = stock;
    }
}