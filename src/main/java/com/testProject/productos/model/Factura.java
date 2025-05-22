package com.testProject.productos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroFactura;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "envio_id", referencedColumnName = "id")
    private Envio envio;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles = new ArrayList<>();

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double totalImpuestos;

    @Column(nullable = false)
    private Double total;


    @PrePersist
    private void prePersist() {
        this.fechaEmision = LocalDateTime.now();
        calcularTotales();
    }

    @PreUpdate
    private void preUpdate() {
        calcularTotales();
    }

    public void calcularTotales() {
      
        this.subtotal = this.detalles != null ? 
            this.detalles.stream()
                .mapToDouble(d -> {
                    if(d.getSubtotal() == null && d instanceof DetalleFactura) {
                        ((DetalleFactura) d).calcularSubtotal();
                    }
                    return d.getSubtotal() != null ? d.getSubtotal() : 0.0;
                })
                .sum() : 
            0.0;

        this.totalImpuestos = this.subtotal;

        double costoEnvio = (this.envio != null && this.envio.getPrecio() != null) ? 
                          this.envio.getPrecio() : 0.0;
        this.total = this.subtotal + this.totalImpuestos + costoEnvio;
    }

    public void agregarDetalle(DetalleFactura detalle) {
        if(this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        detalle.setFactura(this);
        this.detalles.add(detalle);
        calcularTotales();
    }
}