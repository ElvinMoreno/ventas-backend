package com.testProject.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.testProject.productos.model.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    @Query("SELECT f FROM Factura f JOIN FETCH f.cliente WHERE f.cliente.cedula = :cedula")
    List<Factura> findByClienteCedula(@Param("cedula") String cedula);
}