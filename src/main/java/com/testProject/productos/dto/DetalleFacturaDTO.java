package com.testProject.productos.dto;

import com.testProject.productos.model.DetalleFactura;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetalleFacturaDTO {
    private Long id;
    private ProductoTallaDTO productoTalla;
    private Integer cantidad;
    private Double precioUnitario;
    private Double descuento;
    private Double subtotal;

    public DetalleFacturaDTO(DetalleFactura detalle) {
        this.id = detalle.getId();
        this.productoTalla = new ProductoTallaDTO(detalle.getProductoTalla());
        this.cantidad = detalle.getCantidad();
        this.precioUnitario = detalle.getPrecioUnitario();
        this.descuento = detalle.getDescuento();
        this.subtotal = detalle.getSubtotal();
    }
}