package com.testProject.productos.dto;

import lombok.Data;

@Data
public class ProductoRequestDTO {
    private String nombre;
    private String talla;
    private String color;
    private Integer stock;
    private String categoria;
    private Double precio; 
    }