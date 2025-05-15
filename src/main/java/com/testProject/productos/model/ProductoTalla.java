package com.testProject.productos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto_tallas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTalla {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "producto_color_id", nullable = false)
	    private ProductoColor productoColor;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "talla_id", nullable = false)
	    private Talla talla;

	    @Column(nullable = false)
	    private Integer stock = 0; 

	   
	    public ProductoTalla(ProductoColor productoColor, Talla talla, Integer stock) {
	        this.productoColor = productoColor;
	        this.talla = talla;
	        this.stock = stock != null ? stock : 0;
	    }
    
    public void reducirStock(Integer cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
            productoColor.actualizarStockColor();
        } else {
            throw new RuntimeException("Stock insuficiente en talla " + talla.getNombre());
        }
    }
}