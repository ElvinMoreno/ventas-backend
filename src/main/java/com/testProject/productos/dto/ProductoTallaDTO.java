package com.testProject.productos.dto;

import com.testProject.productos.model.ProductoTalla;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoTallaDTO {
    private Long id;
    private ProductoDTO producto;
    private ColorDTO color;
    private TallaDTO talla;

    public ProductoTallaDTO(ProductoTalla productoTalla) {
        this.id = productoTalla.getId();
        if(productoTalla.getProductoColor() != null) {
            if(productoTalla.getProductoColor().getProducto() != null) {
                this.producto = new ProductoDTO(productoTalla.getProductoColor().getProducto());
            }
            if(productoTalla.getProductoColor().getColor() != null) {
                this.color = new ColorDTO(productoTalla.getProductoColor().getColor());
            }
        }
        if(productoTalla.getTalla() != null) {
            this.talla = new TallaDTO(productoTalla.getTalla());
        }
    }
}