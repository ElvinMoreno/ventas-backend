package com.testProject.productos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.testProject.productos.dto.DetalleFacturaRequest;
import com.testProject.productos.dto.FacturaRequest;
import com.testProject.productos.dto.FacturaResponseDTO;
import com.testProject.productos.model.Cliente;
import com.testProject.productos.model.DetalleFactura;
import com.testProject.productos.model.Envio;
import com.testProject.productos.model.Factura;
import com.testProject.productos.model.ProductoTalla;
import com.testProject.productos.repository.ClienteRepository;
import com.testProject.productos.repository.EnvioRepository;
import com.testProject.productos.repository.FacturaRepository;
import com.testProject.productos.repository.ProductoTallaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FacturacionService {
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoTallaRepository productoTallaRepository;
    private final EnvioRepository envioRepository;

    @Autowired
    public FacturacionService(FacturaRepository facturaRepository,
                            ClienteRepository clienteRepository,
                            ProductoTallaRepository productoTallaRepository,
                            EnvioRepository envioRepository) {
        this.facturaRepository = facturaRepository;
        this.clienteRepository = clienteRepository;
        this.productoTallaRepository = productoTallaRepository;
        this.envioRepository = envioRepository;
    }

    public FacturaResponseDTO crearFactura(FacturaRequest request) {
     
        Cliente cliente = clienteRepository.findByCedula(request.getCedulaCliente())
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente();
                    nuevo.setNombre(request.getNombreCliente());
                    nuevo.setApellido(request.getApellidoCliente());
                    nuevo.setCedula(request.getCedulaCliente());
                    nuevo.setDireccion(request.getDireccionCliente());
                    return clienteRepository.save(nuevo);
                });

        Factura factura = new Factura();
        factura.setNumeroFactura(generarNumeroFactura());
        factura.setCliente(cliente);
        factura.setDetalles(new ArrayList<>());

        if (request.getPrecioEnvio() != null && request.getPrecioEnvio() > 0) {
            Envio envio = new Envio();
            envio.setUbicacion(request.getDireccionEnvio());
            envio.setPrecio(request.getPrecioEnvio());
            factura.setEnvio(envioRepository.save(envio));
        }

       
        for (DetalleFacturaRequest detalleReq : request.getDetalles()) {
            ProductoTalla productoTalla = productoTallaRepository.findById(detalleReq.getProductoTallaId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            DetalleFactura detalle = new DetalleFactura();
            detalle.setProductoTalla(productoTalla);
            detalle.setCantidad(detalleReq.getCantidad());
            detalle.setPrecioUnitario(productoTalla.getProductoColor().getProducto().getPrecio());
            detalle.setDescuento(detalleReq.getDescuento() != null ? detalleReq.getDescuento() : 0.0);
            detalle.calcularSubtotal(); 
            
            factura.agregarDetalle(detalle);
            
            productoTalla.reducirStock(detalleReq.getCantidad());
            productoTallaRepository.save(productoTalla);
        }

        factura.calcularTotales();
        Factura facturaGuardada = facturaRepository.save(factura);
        
        return new FacturaResponseDTO(facturaGuardada);
    }

    private String generarNumeroFactura() {
        
        return "FAC-" + System.currentTimeMillis();
    }
    
    public List<FacturaResponseDTO> obtenerFacturasPorCliente(String cedula) {
        return facturaRepository.findByClienteCedula(cedula).stream()
                .map(FacturaResponseDTO::new)
                .collect(Collectors.toList());
    }
}