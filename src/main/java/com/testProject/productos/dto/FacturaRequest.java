package com.testProject.productos.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FacturaRequest {
    @NotBlank
    private String nombreCliente;
    
    @NotBlank
    private String apellidoCliente;
    
    @NotBlank
    private String cedulaCliente;
    
    @NotBlank
    private String direccionCliente;
    
    private String direccionEnvio;
    private Double precioEnvio;
    
    @NotEmpty
    private List<DetalleFacturaRequest> detalles;
}