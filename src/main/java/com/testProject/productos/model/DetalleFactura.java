package com.testProject.productos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "detalles_factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_talla_id", nullable = false)
    private ProductoTalla productoTalla;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double descuento = 0.0;

    @Column(nullable = false)
    private Double subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if(this.precioUnitario == null || this.cantidad == null) {
            this.subtotal = 0.0;
            return;
        }
        
        double desc = (this.descuento != null) ? this.descuento : 0.0;
        this.subtotal = (this.precioUnitario * this.cantidad) - desc;
    }
}