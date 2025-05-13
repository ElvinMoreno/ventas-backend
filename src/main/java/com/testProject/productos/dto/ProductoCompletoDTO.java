package com.testProject.productos.dto;

import lombok.Data;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoCompletoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoria;
    
  
    private List<VarianteDTO> variantes;
    
 
    
    @Data
    public static class VarianteDTO {
        private String color;
        private String talla;
        private Integer stock;

    }
}