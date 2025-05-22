package com.testProject.productos.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.testProject.productos.model.Factura;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacturaResponseDTO {
    private Long id;
    private String numeroFactura;
    private LocalDateTime fechaEmision;
    private ClienteDTO cliente;
    private EnvioDTO envio;
    private List<DetalleFacturaDTO> detalles;
    private Double subtotal;
    private Double totalImpuestos;
    private Double total;

    public FacturaResponseDTO(Factura factura) {
        this.id = factura.getId();
        this.numeroFactura = factura.getNumeroFactura();
        this.fechaEmision = factura.getFechaEmision();
        this.cliente = new ClienteDTO(factura.getCliente());
        this.envio = factura.getEnvio() != null ? new EnvioDTO(factura.getEnvio()) : null;
        this.detalles = factura.getDetalles().stream()
                .map(DetalleFacturaDTO::new)
                .collect(Collectors.toList());
        this.subtotal = factura.getSubtotal();
        this.totalImpuestos = factura.getTotalImpuestos();
        this.total = factura.getTotal();
    }
}