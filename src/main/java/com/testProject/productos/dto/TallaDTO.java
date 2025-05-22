package com.testProject.productos.dto;

import com.testProject.productos.model.Talla;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TallaDTO {
    private Long id;
    private String nombre;

    public TallaDTO(Talla talla) {
        this.id = talla.getId();
        this.nombre = talla.getNombre();
    }
}