package com.testProject.productos.dto;

import com.testProject.productos.model.ProductoTalla;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetallePagoPendienteDTO {
    private ProductoTallaResponseDTO producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double descuento;
    private Double subtotal;

    public DetallePagoPendienteDTO(ProductoTalla productoTalla, Integer cantidad, 
                                 Double precioUnitario, Double descuento) {
        this.producto = new ProductoTallaResponseDTO(productoTalla);
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuento = descuento != null ? descuento : 0.0;
        this.subtotal = (precioUnitario - this.descuento) * cantidad;
    }
}