package com.testProject.productos.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testProject.productos.dto.ClienteDTO;
import com.testProject.productos.dto.DetalleFacturaRequest;
import com.testProject.productos.dto.DetallePagoPendienteDTO;
import com.testProject.productos.dto.EnvioDTO;
import com.testProject.productos.dto.FacturaRequest;
import com.testProject.productos.dto.FacturaResponseDTO;
import com.testProject.productos.dto.PagoPendienteDetalladoDTO;
import com.testProject.productos.dto.PagoPendienteResponseDTO;
import com.testProject.productos.model.Cliente;
import com.testProject.productos.model.DetalleFactura;
import com.testProject.productos.model.Envio;
import com.testProject.productos.dto.EstadoPago;
import com.testProject.productos.model.Factura;
import com.testProject.productos.model.PagoPendiente;
import com.testProject.productos.model.ProductoTalla;
import com.testProject.productos.repository.ClienteRepository;
import com.testProject.productos.repository.EnvioRepository;
import com.testProject.productos.repository.FacturaRepository;
import com.testProject.productos.repository.PagoPendienteRepository;
import com.testProject.productos.repository.ProductoTallaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FacturacionService {
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoTallaRepository productoTallaRepository;
    private final EnvioRepository envioRepository;
    private final PagoPendienteRepository pagoPendienteRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public FacturacionService(FacturaRepository facturaRepository,
                            ClienteRepository clienteRepository,
                            ProductoTallaRepository productoTallaRepository,
                            EnvioRepository envioRepository,
                            PagoPendienteRepository pagoPendienteRepository,
                            ObjectMapper objectMapper) {
        this.facturaRepository = facturaRepository;
        this.clienteRepository = clienteRepository;
        this.productoTallaRepository = productoTallaRepository;
        this.envioRepository = envioRepository;
        this.pagoPendienteRepository = pagoPendienteRepository;
        this.objectMapper = objectMapper;
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
    
    
    public PagoPendienteDetalladoDTO obtenerDatosPagoPendiente(String codigoTransaccion) throws JsonProcessingException {

        Optional<PagoPendiente> pagoOpt = pagoPendienteRepository.findByCodigoTransaccion(codigoTransaccion);
        
        if (!pagoOpt.isPresent()) {
            PagoPendiente dummyPago = new PagoPendiente();
            dummyPago.setCodigoTransaccion(codigoTransaccion);
            dummyPago.setEstado(EstadoPago.RECHAZADO);
            
            return new PagoPendienteDetalladoDTO(
                dummyPago,
                null, 
                null, 
                Collections.emptyList(), 
                0.0, 
                0.0 
            );
        }
        
        PagoPendiente pago = pagoPendienteRepository.findByCodigoTransaccion(codigoTransaccion)
        .orElseThrow(() -> new RuntimeException("código " + codigoTransaccion + " no existe"));
        
        FacturaRequest request = objectMapper.readValue(pago.getDatosFactura(), FacturaRequest.class);
        
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre(request.getNombreCliente());
        clienteDTO.setApellido(request.getApellidoCliente());
        clienteDTO.setCedula(request.getCedulaCliente());
        clienteDTO.setDireccion(request.getDireccionCliente());        
       
        EnvioDTO envioDTO = null;
        if (request.getPrecioEnvio() != null && request.getPrecioEnvio() > 0) {
            envioDTO = new EnvioDTO();
            envioDTO.setUbicacion(request.getDireccionEnvio());
            envioDTO.setPrecio(request.getPrecioEnvio());
        }
        
        List<DetallePagoPendienteDTO> detallesDTO = new ArrayList<>();
        double subtotal = 0.0;
        
        for (DetalleFacturaRequest detalleReq : request.getDetalles()) {
            ProductoTalla productoTalla = productoTallaRepository.findById(detalleReq.getProductoTallaId())
                    .orElseThrow(() -> new RuntimeException("Producto con ID " + detalleReq.getProductoTallaId() + " no encontrado"));
            
            double precioUnitario = productoTalla.getProductoColor().getProducto().getPrecio();
            double descuento = detalleReq.getDescuento() != null ? detalleReq.getDescuento() : 0.0;
            double subtotalDetalle = (precioUnitario - descuento) * detalleReq.getCantidad();
            
            detallesDTO.add(new DetallePagoPendienteDTO(
                productoTalla, 
                detalleReq.getCantidad(), 
                precioUnitario, 
                detalleReq.getDescuento()
            ));
            
            subtotal += subtotalDetalle;
        }
        
        double total = subtotal;
        if (envioDTO != null) {
            total += envioDTO.getPrecio();
        }
        
        return new PagoPendienteDetalladoDTO(
            pago, 
            clienteDTO, 
            envioDTO, 
            detallesDTO, 
            subtotal, 
            total
        );
    }

    public FacturaResponseDTO completarPago(String codigoTransaccion) throws JsonProcessingException {
        PagoPendiente pago = pagoPendienteRepository.findByCodigoTransaccion(codigoTransaccion)
                .orElseThrow(() -> new RuntimeException("Pago pendiente no encontrado"));
        
        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new RuntimeException("El pago ya ha sido procesado");
        }
        
        FacturaRequest request = objectMapper.readValue(pago.getDatosFactura(), FacturaRequest.class);
        Factura facturaGuardada = crearFacturaEntity(request); 
        
        pago.setEstado(EstadoPago.COMPLETADO);
        pagoPendienteRepository.save(pago);
        
        return new FacturaResponseDTO(facturaGuardada, pago.getCodigoTransaccion());
    }
    
    private Factura crearFacturaEntity(FacturaRequest request) {
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
        return facturaRepository.save(factura);
    }
    
    public PagoPendienteResponseDTO crearPagoPendiente(FacturaRequest request) throws JsonProcessingException {
        String datosFactura = objectMapper.writeValueAsString(request);
        
        PagoPendiente pago = new PagoPendiente();
        pago.setDatosFactura(datosFactura);
        
        PagoPendiente guardado = pagoPendienteRepository.save(pago);
        return new PagoPendienteResponseDTO(guardado);
    }
    
}