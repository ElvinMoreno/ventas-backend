package com.testProject.productos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.testProject.productos.model.Talla;

public interface TallaRepository extends JpaRepository<Talla, Long> {
	Optional<Talla> findByNombre(String nombre);
}