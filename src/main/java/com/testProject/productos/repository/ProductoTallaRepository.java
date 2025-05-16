package com.testProject.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.testProject.productos.model.ProductoColor;
import com.testProject.productos.model.ProductoTalla;
import com.testProject.productos.model.Talla;


public interface ProductoTallaRepository extends 
    JpaRepository<ProductoTalla, Long>, 
    JpaSpecificationExecutor<ProductoTalla> {
    List<ProductoTalla> findByProductoColorId(Long productoColorId);
    Optional<ProductoTalla> findByProductoColorAndTalla(ProductoColor productoColor, Talla talla);
    
    @Query("SELECT pt FROM ProductoTalla pt JOIN pt.productoColor pc JOIN pc.producto p WHERE p.nombre = :nombre")
    List<ProductoTalla> findByProductoNombre(@Param("nombre") String nombre);
    
    @Query("SELECT pt FROM ProductoTalla pt " +
            "JOIN pt.productoColor pc " +
            "JOIN pc.producto p " +
            "JOIN p.categoria c " +
            "JOIN pc.color col " +
            "JOIN pt.talla t " +
            "WHERE LOWER(p.nombre) LIKE %:nombre% " +
            "AND LOWER(c.nombre) = :categoria " +
            "AND LOWER(col.nombre) = :color " +
            "AND LOWER(t.nombre) = :talla")
     Optional<ProductoTalla> findByProductoNombreYCategoriaYColorYTalla(
             @Param("nombre") String nombre,
             @Param("categoria") String categoria,
             @Param("color") String color,
             @Param("talla") String talla);
    
    @Query("SELECT pt FROM ProductoTalla pt " +
            "JOIN pt.productoColor pc " +
            "JOIN pc.producto p " +
            "WHERE LOWER(p.nombre) LIKE %:nombre%")
     List<ProductoTalla> findByProductoNombreConteniendo(@Param("nombre") String nombre);

    
    
}