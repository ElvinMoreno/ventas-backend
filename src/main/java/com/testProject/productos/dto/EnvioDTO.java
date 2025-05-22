package com.testProject.productos.dto;

import com.testProject.productos.model.Envio;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnvioDTO {
    private Long id;
    private String ubicacion;
    private Double precio;

    public EnvioDTO(Envio envio) {
        this.id = envio.getId();
        this.ubicacion = envio.getUbicacion();
        this.precio = envio.getPrecio();
    }
    
    public EnvioDTO(Long id, String ubicacion, Double precio) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.precio = precio;
    }
}