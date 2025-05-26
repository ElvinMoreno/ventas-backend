package com.testProject.productos.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.testProject.productos.model.PagoPendiente;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagoPendienteDetalladoDTO {
    private String codigoTransaccion;
    private LocalDateTime fechaCreacion;
    private EstadoPago estado;
    private ClienteDTO cliente;
    private EnvioDTO envio;
    private List<DetallePagoPendienteDTO> detalles;
    private Double subtotal;
    private Double total;

    public PagoPendienteDetalladoDTO(PagoPendiente pago, ClienteDTO cliente, 
                                   EnvioDTO envio, List<DetallePagoPendienteDTO> detalles,
                                   Double subtotal, Double total) {
        this.codigoTransaccion = pago.getCodigoTransaccion();
        this.fechaCreacion = pago.getFechaCreacion();
        this.estado = pago.getEstado();
        this.cliente = cliente;
        this.envio = envio;
        this.detalles = detalles;
        this.subtotal = subtotal;
        this.total = total;
    }
}
