package com.testProject.productos.service;


import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.testProject.productos.dto.FiltroPrenda;
import com.testProject.productos.dto.ProductoCompletoDTO;
import com.testProject.productos.dto.ProductoCompletoDTO.VarianteDTO;
import com.testProject.productos.dto.ProductoConsultaDTO;
import com.testProject.productos.dto.ProductoImagenDTO;
import com.testProject.productos.dto.ProductoRequestDTO;
import com.testProject.productos.dto.ProductoResponseDTO;
import com.testProject.productos.model.*;
import com.testProject.productos.repository.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Transactional
public class ProductoService {
	
	@PersistenceContext
    private EntityManager entityManager;
	
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ColorRepository colorRepository;
    private final TallaRepository tallaRepository;
    private final ProductoColorRepository productoColorRepository;
    private final ProductoTallaRepository productoTallaRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    public ProductoService(ProductoRepository productoRepository,
                         CategoriaRepository categoriaRepository,
                         ColorRepository colorRepository,
                         TallaRepository tallaRepository,
                         ProductoColorRepository productoColorRepository,
                         ProductoTallaRepository productoTallaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.colorRepository = colorRepository;
        this.tallaRepository = tallaRepository;
        this.productoColorRepository = productoColorRepository;
        this.productoTallaRepository = productoTallaRepository;
    }
    public Page<ProductoCompletoDTO> obtenerTodosLosProductosPaginados(int pagina, int tamaño) {
        
        Page<Long> idsPagina = productoRepository.findProductoIds(PageRequest.of(pagina, tamaño));
        
        
        List<Producto> productos = productoRepository.findAllWithColoresByIds(idsPagina.getContent());
        
        
        List<ProductoColor> colores = productoColorRepository.findAllWithTallasByProductoIds(idsPagina.getContent());
        
       
        Map<Long, Producto> productoMap = productos.stream()
            .collect(Collectors.toMap(Producto::getId, Function.identity()));
        
        colores.forEach(pc -> {
            Producto p = productoMap.get(pc.getProducto().getId());
            if (p != null) {
                p.getColores().add(pc);
            }
        });
        
        return idsPagina.map(id -> mapearProductoADTO(productoMap.get(id)));
    }
    

    public List<ProductoCompletoDTO> obtenerTodosLosProductos() {
       
        List<Long> ids = productoRepository.findAllProductIds();
        

        List<Producto> productos = productoRepository.findAllWithColoresByIds(ids);
        
        List<ProductoColor> colores = productoColorRepository.findAllWithTallasByProductoIds(ids);
      
        Map<Long, Producto> productoMap = productos.stream()
            .collect(Collectors.toMap(Producto::getId, Function.identity()));
        
        
        colores.forEach(pc -> {
            Producto p = productoMap.get(pc.getProducto().getId());
            if (p != null) {
              
                boolean colorExiste = p.getColores().stream()
                    .anyMatch(existente -> existente.getId().equals(pc.getId()));
                
                if (!colorExiste) {
                    p.getColores().add(pc);
                }
            }
        });
      
        return productoMap.values().stream()
            .map(this::mapearProductoADTO)
            .collect(Collectors.toList());
    }

    
    public ProductoConsultaDTO consultarPrenda(FiltroPrenda filtro) {
        validarFiltro(filtro);
        
        if (soloNombre(filtro)) {
            return consultarPorNombre(filtro.getNombre());
        }
        
        List<ProductoTalla> variantes = buscarVariantes(filtro);
        
        if (variantes.isEmpty()) {
            throw new RuntimeException("No se encontraron prendas con los filtros especificados");
        }
        
        return construirRespuesta(filtro, variantes);
    }
    
    private void validarFiltro(FiltroPrenda filtro) {
        if (!filtro.tieneNombre()) {
            throw new IllegalArgumentException("El nombre del producto es requerido");
        }
    }
    
    private boolean soloNombre(FiltroPrenda filtro) {
        return !filtro.tieneCategoria() && !filtro.tieneTalla() && !filtro.tieneColor();
    }
    
    private List<ProductoTalla> buscarVariantes(FiltroPrenda filtro) {
     
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
      
        CriteriaQuery<ProductoTalla> cq = cb.createQuery(ProductoTalla.class);
        
      
        Root<ProductoTalla> root = cq.from(ProductoTalla.class);
        
     
        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
        
        
        predicates.add(cb.equal(
            root.get("productoColor").get("producto").get("nombre"), 
            filtro.getNombre()
        ));
        
        if (filtro.tieneCategoria()) {
            predicates.add(cb.equal(
                root.get("productoColor").get("producto").get("categoria").get("nombre"),
                filtro.getCategoria()
            ));
        }
        
        if (filtro.tieneTalla()) {
            predicates.add(cb.equal(
                root.get("talla").get("nombre"),
                filtro.getTalla()
            ));
        }
 
        if (filtro.tieneColor()) {
            predicates.add(cb.equal(
                root.get("productoColor").get("color").get("nombre"),
                filtro.getColor()
            ));
        }
        
       
        cq.where(cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
        
        return entityManager.createQuery(cq).getResultList();
    }
    
    private ProductoConsultaDTO consultarPorNombre(String nombre) {
        List<Producto> productos = productoRepository.findByNombreExacto(nombre);
        
        ProductoConsultaDTO dto = new ProductoConsultaDTO();
        dto.setNombre(nombre);
        dto.setPrecio(productos.get(0).getPrecio());

        List<ProductoTalla> variantes = productoTallaRepository.findByProductoNombre(nombre);
        
        extraerDatosDisponibles(dto, variantes);
        return dto;
    }
    
    private ProductoConsultaDTO construirRespuesta(FiltroPrenda filtro, List<ProductoTalla> variantes) {
        ProductoConsultaDTO dto = new ProductoConsultaDTO();
        ProductoTalla primeraVariante = variantes.get(0);
        Producto producto = primeraVariante.getProductoColor().getProducto();
        
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        
        if (filtro.tieneCategoria() && filtro.tieneTalla() && filtro.tieneColor()) {
            dto.setCategoria(filtro.getCategoria());
            dto.setTalla(filtro.getTalla());
            dto.setColor(filtro.getColor());
            dto.setStock(primeraVariante.getStock());
            return dto;
        }
        
        if (filtro.tieneCategoria()) {
            dto.setCategoria(filtro.getCategoria());
        }
        
        extraerDatosDisponibles(dto, variantes);
        return dto;
    }
    
    private void extraerDatosDisponibles(ProductoConsultaDTO dto, List<ProductoTalla> variantes) {
       
        if (dto.getCategoria() == null) {
            Set<String> categorias = variantes.stream()
                .map(pt -> pt.getProductoColor().getProducto().getCategoria().getNombre())
                .collect(Collectors.toSet());
            dto.setCategoriasDisponibles(new ArrayList<>(categorias));
        }
        

        if (!variantes.isEmpty() && !variantes.get(0).getTalla().getNombre().equals(dto.getTalla())) {
            Set<String> tallas = variantes.stream()
                .map(pt -> pt.getTalla().getNombre())
                .collect(Collectors.toSet());
            dto.setTallasDisponibles(new ArrayList<>(tallas));
        }
        
       
        if (dto.getColor() == null) {
            Map<String, Integer> coloresConStock = variantes.stream()
                .collect(Collectors.groupingBy(
                    pt -> pt.getProductoColor().getColor().getNombre(),
                    Collectors.summingInt(ProductoTalla::getStock)
                ));
            
            List<ProductoConsultaDTO.ColorInfo> colores = coloresConStock.entrySet().stream()
                .map(entry -> {
                    ProductoConsultaDTO.ColorInfo colorInfo = new ProductoConsultaDTO.ColorInfo();
                    colorInfo.setNombre(entry.getKey());
                    colorInfo.setStock(entry.getValue());
                    return colorInfo;
                })
                .collect(Collectors.toList());
            
            dto.setColoresDisponibles(colores);
        }
    }
    

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
   

    public Optional<Producto> obtenerProductoPorNombre(String nombre) {
        return productoRepository.findFirstByNombre(nombre);
    }
    
    @Transactional
    public ProductoResponseDTO crearProductoConVariante(ProductoRequestDTO request) {
        log.info("Iniciando creación de variante para producto: {}", request.getNombre());
        
        try {
            log.debug("Buscando categoría: {}", request.getCategoria());
            Categoria categoria = categoriaRepository.findByNombre(request.getCategoria())
                    .orElseGet(() -> {
                        log.debug("Creando nueva categoría: {}", request.getCategoria());
                        Categoria nueva = new Categoria();
                        nueva.setNombre(request.getCategoria());
                        return categoriaRepository.save(nueva);
                    });

            log.debug("Buscando producto: {} en categoría: {}", request.getNombre(), categoria.getNombre());
            Producto producto = productoRepository.findByNombreAndCategoria(request.getNombre(), categoria)
                    .orElseGet(() -> {
                        log.debug("Creando nuevo producto: {}", request.getNombre());
                        Producto nuevo = new Producto();
                        nuevo.setNombre(request.getNombre());
                        nuevo.setCategoria(categoria);
                        nuevo.setPrecio(request.getPrecio());
                        nuevo.setColores(new ArrayList<>());
                        return productoRepository.save(nuevo);
                    });

            log.debug("Buscando color: {}", request.getColor());
            Color color = colorRepository.findByNombre(request.getColor())
                    .orElseGet(() -> {
                        log.debug("Creando nuevo color: {}", request.getColor());
                        Color nuevo = new Color();
                        nuevo.setNombre(request.getColor());
                        return colorRepository.save(nuevo);
                    });

            log.debug("Buscando talla: {}", request.getTalla());
            Talla talla = tallaRepository.findByNombre(request.getTalla())
                    .orElseGet(() -> {
                        log.debug("Creando nueva talla: {}", request.getTalla());
                        Talla nueva = new Talla();
                        nueva.setNombre(request.getTalla());
                        return tallaRepository.save(nueva);
                    });

            log.debug("Buscando combinación producto-color: {} - {}", producto.getNombre(), color.getNombre());
            ProductoColor productoColor = productoColorRepository.findByProductoAndColor(producto, color)
                    .orElseGet(() -> {
                        log.debug("Creando nueva combinación producto-color");
                        ProductoColor pc = new ProductoColor();
                        pc.setProducto(producto);
                        pc.setColor(color);
                        pc.setTallas(new ArrayList<>());
                        producto.getColores().add(pc);
                        return productoColorRepository.save(pc);
                    });

            log.debug("Buscando combinación producto-talla: {} - {}", color.getNombre(), talla.getNombre());
            ProductoTalla productoTalla = productoTallaRepository.findByProductoColorAndTalla(productoColor, talla)
                    .orElseGet(() -> {
                        log.debug("Creando nueva combinación producto-talla");
                        ProductoTalla pt = new ProductoTalla();
                        pt.setProductoColor(productoColor);
                        pt.setTalla(talla);
                        pt.setStock(0);
                        
                        if(productoColor.getTallas() == null) {
                            productoColor.setTallas(new ArrayList<>());
                        }
                        productoColor.getTallas().add(pt);
                        
                        return productoTallaRepository.save(pt);
                    });

            int nuevoStock = productoTalla.getStock() + request.getStock();
            log.debug("Actualizando stock de {} de {} a {}", 
                    productoTalla.getId(), productoTalla.getStock(), nuevoStock);
            productoTalla.setStock(nuevoStock);
            productoTallaRepository.save(productoTalla);

            productoColor.actualizarStockColor();
            producto.actualizarStockTotal();

            log.info("Variante creada exitosamente. Stock actualizado para {} {} {}: {}", 
                    producto.getNombre(), color.getNombre(), talla.getNombre(), nuevoStock);

            return new ProductoResponseDTO(
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getCategoria().getNombre(),
                    color.getNombre(),
                    talla.getNombre(),
                    productoTalla.getStock()
            );

        } catch (Exception e) {
            log.error("Error al crear variante para producto {}: {}", request.getNombre(), e.getMessage(), e);
            throw new RuntimeException("Error al crear variante: " + e.getMessage(), e);
        }
    }

    private ProductoCompletoDTO mapearProductoADTO(Producto producto) {
        ProductoCompletoDTO dto = new ProductoCompletoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
      
        dto.setPrecio(producto.getPrecio());
        
        if(producto.getCategoria() != null) {
            dto.setCategoria(producto.getCategoria().getNombre());
        }
        
        if(producto.getColores() != null) {

            Set<VarianteDTO> variantesUnicas = new HashSet<>();
            
            producto.getColores().forEach(pc -> {
                if (pc.getTallas() != null) {
                    pc.getTallas().forEach(pt -> {
                        VarianteDTO v = new VarianteDTO();
                        v.setColor(pc.getColor() != null ? pc.getColor().getNombre() : "Sin color");
                        v.setTalla(pt.getTalla() != null ? pt.getTalla().getNombre() : "Sin talla");
                        v.setStock(pt.getStock());
                        variantesUnicas.add(v);
                    });
                }
            });
            
            dto.setVariantes(new ArrayList<>(variantesUnicas));
        }
        
        return dto;
    }
    
    public ProductoImagenDTO obtenerImagenProducto(String nombre, String categoria, String color, String talla) {
        
        Optional<String> imagenUrl = productoColorRepository
            .findImagenUrlByProductoNombreAndCategoriaAndColor(nombre, categoria, color);
        
        if (imagenUrl.isEmpty()) {
            throw new RuntimeException("No se encontró imagen para el producto con los parámetros especificados");
        }
        
        return new ProductoImagenDTO(nombre, categoria, color, talla, imagenUrl.get());
    }




}