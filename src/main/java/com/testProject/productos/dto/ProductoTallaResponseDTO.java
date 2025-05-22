package com.testProject.productos.dto;

import com.testProject.productos.model.ProductoColor;
import com.testProject.productos.model.ProductoTalla;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTallaResponseDTO {
    private Long id;
    private ProductoDTO producto;
    private ColorDTO color;  
    private TallaDTO talla;
    private Integer stock;   

    public ProductoTallaResponseDTO(ProductoTalla productoTalla) {
        this.id = productoTalla.getId();
        ProductoColor productoColor = productoTalla.getProductoColor();
        
        if(productoColor != null) {
            this.producto = new ProductoDTO(productoColor.getProducto());
            this.color = new ColorDTO(productoColor.getColor());
        }
        
        this.talla = new TallaDTO(productoTalla.getTalla());
        this.stock = productoTalla.getStock();
    }
}