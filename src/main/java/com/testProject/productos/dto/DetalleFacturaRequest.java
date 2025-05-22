package com.testProject.productos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleFacturaRequest {
    @NotNull
    private Long productoTallaId;
    
    @Min(1)
    private Integer cantidad;
    
    @Min(0)
    private Double descuento;
}