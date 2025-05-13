package com.testProject.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.testProject.productos.model.Color;
import com.testProject.productos.model.Producto;
import com.testProject.productos.model.ProductoColor;

import java.util.List;
import java.util.Optional;

public interface ProductoColorRepository extends JpaRepository<ProductoColor, Long> {
    List<ProductoColor> findByProductoId(Long productoId);
    
    Optional<ProductoColor> findByProductoAndColor(Producto producto, Color color);
    
    @Query("SELECT DISTINCT pc FROM ProductoColor pc LEFT JOIN FETCH pc.tallas WHERE pc.producto.id IN :productoIds")
    List<ProductoColor> findAllWithTallasByProductoIds(@Param("productoIds") List<Long> productoIds);
    
    @Query("SELECT pc.imagenUrl FROM ProductoColor pc " +
            "JOIN pc.producto p " +
            "JOIN p.categoria c " +
            "JOIN pc.color col " +
            "WHERE p.nombre = :nombre " +
            "AND c.nombre = :categoria " +
            "AND col.nombre = :color")
     Optional<String> findImagenUrlByProductoNombreAndCategoriaAndColor(
             @Param("nombre") String nombre,
             @Param("categoria") String categoria,
             @Param("color") String color);
}