package com.testProject.productos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.testProject.productos.model.Color;

public interface ColorRepository extends JpaRepository<Color, Long> {
	Optional<Color> findByNombre(String nombre);
}
