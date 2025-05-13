package com.testProject.productos.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoConsultaDTO {
    private String nombre;
    private Double precio;
    private String categoria;
    private String talla;
    private String color;
    private Integer stock;
    

    private List<String> categoriasDisponibles;
    private List<String> tallasDisponibles;
    private List<ColorInfo> coloresDisponibles;
    
    @Data
    public static class ColorInfo {
        private String nombre;
        private Integer stock;
    }
}