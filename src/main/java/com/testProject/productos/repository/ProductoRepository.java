package com.testProject.productos.repository;

import com.testProject.productos.model.Categoria;
import com.testProject.productos.model.Producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
   
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    
    @Query("SELECT DISTINCT p FROM Producto p WHERE p.nombre = :nombre")
    List<Producto> findByNombreExacto(@Param("nombre") String nombre);
    

    Optional<Producto> findFirstByNombre(String nombre);
    
   
    @Query("SELECT DISTINCT p FROM Producto p JOIN p.categoria c JOIN p.colores pc JOIN pc.color col " +
           "WHERE p.nombre = :nombre AND c.nombre = :categoria AND col.nombre = :color")
    List<Producto> findByNombreAndCategoriaAndColor(
            @Param("nombre") String nombre,
            @Param("categoria") String categoria,
            @Param("color") String color);

    @Query("SELECT DISTINCT p FROM Producto p JOIN p.colores pc JOIN pc.color col " +
           "WHERE p.nombre = :nombre AND col.nombre = :color")
    List<Producto> findByNombreAndColor(
            @Param("nombre") String nombre,
            @Param("color") String color);
    
    @Query("SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.colores pc " +
            "LEFT JOIN FETCH pc.tallas pt " +
            "LEFT JOIN FETCH p.categoria " +
            "LEFT JOIN FETCH pc.color " +
            "LEFT JOIN FETCH pt.talla")
     List<Producto> findAllWithVariantes();
     
   
     @Query("SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.colores pc " +
            "LEFT JOIN FETCH pc.tallas pt " +
            "LEFT JOIN FETCH p.categoria " +
            "LEFT JOIN FETCH pc.color " +
            "LEFT JOIN FETCH pt.talla")
     Page<Producto> findAllWithVariantes(Pageable pageable);
     
     @Query("SELECT DISTINCT p FROM Producto p " +
    	       "LEFT JOIN FETCH p.colores pc " +
    	       "LEFT JOIN FETCH pc.tallas pt " +
    	       "LEFT JOIN FETCH p.categoria " +
    	       "LEFT JOIN FETCH pc.color " +
    	       "LEFT JOIN FETCH pt.talla " +
    	       "WHERE p.id IN :ids")
    	List<Producto> findAllWithVariantesByIds(@Param("ids") List<Long> ids);
     
     @Query("SELECT p FROM Producto p LEFT JOIN p.colores LEFT JOIN p.categoria")
     Page<Producto> findAllForPagination(Pageable pageable);
     
     @Query("SELECT p.id FROM Producto p")
     Page<Long> findProductoIds(Pageable pageable);

     @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.colores WHERE p.id IN :ids")
     List<Producto> findAllWithColoresByIds(@Param("ids") List<Long> ids);
     
     Optional<Producto> findByNombreAndCategoria(String nombre, Categoria categoria);
     
     @Query("SELECT p FROM Producto p WHERE p.nombre = :nombre AND p.categoria.nombre = :categoria")
     Optional<Producto> findByNombreAndCategoriaNombre(
             @Param("nombre") String nombre,
             @Param("categoria") String categoria);
     
     @Query("SELECT p.id FROM Producto p")
     List<Long> findAllProductIds();
     
     @Query("SELECT DISTINCT p FROM Producto p " +
             "WHERE LOWER(p.nombre) LIKE %:nombre%")
      List<Producto> findByNombreConteniendo(@Param("nombre") String nombre);
      
      @Query("SELECT DISTINCT p FROM Producto p JOIN p.categoria c " +
             "WHERE LOWER(p.nombre) LIKE %:nombre% " +
             "AND LOWER(c.nombre) = :categoria")
      List<Producto> findByNombreConteniendoYCategoria(
              @Param("nombre") String nombre,
              @Param("categoria") String categoria);
     
}