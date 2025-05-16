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
    
    public String getNombreNormalizado() {
        return nombre != null ? nombre.trim().toLowerCase() : null;
    }
    
    public String getCategoriaNormalizada() {
        return categoria != null ? categoria.trim().toLowerCase() : null;
    }
    
    public String getColorNormalizado() {
        return color != null ? color.trim().toLowerCase() : null;
    }
    
    public String getTallaNormalizada() {
        return talla != null ? talla.trim().toLowerCase() : null;
    }
}