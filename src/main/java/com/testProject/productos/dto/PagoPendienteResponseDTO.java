package com.testProject.productos.dto;

import java.time.LocalDateTime;

import com.testProject.productos.model.PagoPendiente;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagoPendienteResponseDTO {
    private String codigoTransaccion;
    private LocalDateTime fechaCreacion;
    private EstadoPago estado;
    
    public PagoPendienteResponseDTO(PagoPendiente pago) {
        this.codigoTransaccion = pago.getCodigoTransaccion();
        this.fechaCreacion = pago.getFechaCreacion();
        this.estado = pago.getEstado();
    }
}