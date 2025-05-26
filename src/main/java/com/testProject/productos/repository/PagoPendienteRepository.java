package com.testProject.productos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testProject.productos.model.PagoPendiente;

public interface PagoPendienteRepository extends JpaRepository<PagoPendiente, Long> {
    Optional<PagoPendiente> findByCodigoTransaccion(String codigoTransaccion);
}