package com.testProject.productos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductoRequestDTO {
    @NotBlank
    private String nombre;
    
    @NotBlank
    private String categoria;
    
    @NotBlank
    private String color;
    
    @NotBlank
    private String talla;
    
    private Integer stock;
    
 
    private Double precio;
}


