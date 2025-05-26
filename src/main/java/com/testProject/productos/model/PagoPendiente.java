package com.testProject.productos.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.testProject.productos.dto.EstadoPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagos_pendientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoPendiente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigoTransaccion;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String datosFactura; 
    
    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
        this.codigoTransaccion = "PAG-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
