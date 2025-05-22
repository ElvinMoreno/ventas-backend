package com.testProject.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testProject.productos.model.Envio;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
  
}
