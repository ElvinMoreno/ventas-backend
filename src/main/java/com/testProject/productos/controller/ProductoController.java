package com.testProject.productos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.testProject.productos.dto.ApiResponseDTO;
import com.testProject.productos.dto.FiltroPrenda;
import com.testProject.productos.dto.ProductoCompletoDTO;
import com.testProject.productos.dto.ProductoConsultaDTO;
import com.testProject.productos.dto.ProductoImagenDTO;
import com.testProject.productos.dto.ProductoRequestDTO;
import com.testProject.productos.dto.ProductoResponseDTO;
import com.testProject.productos.model.Producto;
import com.testProject.productos.service.ProductoService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/productos")
@Slf4j 
public class ProductoController {
    private final ProductoService productoService;

    
    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    
    @GetMapping
    public ResponseEntity<List<ProductoCompletoDTO>> obtenerTodosLosProductos(
            @RequestParam(required = false) Boolean paginado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (paginado != null && paginado) {
            Page<ProductoCompletoDTO> pagina = productoService.obtenerTodosLosProductosPaginados(page, size);
            return ResponseEntity.ok(pagina.getContent());
        }
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProductoCompletoDTO>> obtenerTodosLosProductosSinPaginacion() {
        List<ProductoCompletoDTO> productos = productoService.obtenerTodosLosProductos();
        
        productos.forEach(p -> {
            long count = p.getVariantes().stream().distinct().count();

        });
        
        return ResponseEntity.ok(productos);
    }
    

    @GetMapping("/consultar")
    public ResponseEntity<ApiResponseDTO<ProductoConsultaDTO>> consultarPrenda(
            @RequestParam String nombre,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) String color) {
        
        try {
            FiltroPrenda filtro = new FiltroPrenda();
            filtro.setNombre(nombre);
            filtro.setCategoria(categoria);
            filtro.setTalla(talla);
            filtro.setColor(color);
            
            ProductoConsultaDTO resultado = productoService.consultarPrenda(filtro);
            return ResponseEntity.ok(ApiResponseDTO.success(resultado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponseDTO.notFound(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponseDTO.notFound(e.getMessage()));
        }
    }
    
    @GetMapping("/imagen")
    public ResponseEntity<ApiResponseDTO<ProductoImagenDTO>> obtenerImagenProducto(
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam String color,
            @RequestParam(required = false) String talla) {
        
        try {
            ProductoImagenDTO resultado = productoService.obtenerImagenProducto(nombre, categoria, color, talla);
            return ResponseEntity.ok(ApiResponseDTO.success(resultado));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponseDTO.notFound(e.getMessage()));
        }
    }
    

    
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductoResponseDTO>> crearProducto(
            @Valid @RequestBody ProductoRequestDTO request) {
        
        try {
       
            if (request.getPrecio() != null && request.getPrecio() <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a cero");
            }
            
            ProductoResponseDTO resultado = productoService.crearProductoConVariante(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success(resultado));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.notFound(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear producto", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponseDTO.notFound("Error interno al crear el producto"));
        }
    }
       
 
}