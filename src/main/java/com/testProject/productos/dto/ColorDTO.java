package com.testProject.productos.dto;

import com.testProject.productos.model.Color;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorDTO {
    private Long id;
    private String nombre;

    public ColorDTO(Color color) {
        this.id = color.getId();
        this.nombre = color.getNombre();
    }
}