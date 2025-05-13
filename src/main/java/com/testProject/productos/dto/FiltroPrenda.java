package com.testProject.productos.dto;

import lombok.Data;

@Data
public class FiltroPrenda {
    private String nombre;
    private String categoria;
    private String talla;
    private String color;
    
    public boolean tieneNombre() {
        return nombre != null && !nombre.isBlank();
    }
    
    public boolean tieneCategoria() {
        return categoria != null && !categoria.isBlank();
    }
    
    public boolean tieneTalla() {
        return talla != null && !talla.isBlank();
    }
    
    public boolean tieneColor() {
        return color != null && !color.isBlank();
    }
}