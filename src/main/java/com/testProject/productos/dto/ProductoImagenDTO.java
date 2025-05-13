package com.testProject.productos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoImagenDTO {
    private String nombre;
    private String categoria;
    private String color;
    private String talla;
    private String imagenUrl;
    
    public ProductoImagenDTO(String nombre, String categoria, String color, String talla, String imagenUrl) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.color = color;
        this.talla = talla;
        this.imagenUrl = imagenUrl;
    }
}