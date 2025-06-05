package com.testProject.productos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.testProject.productos.dto.ApiResponseDTO;
import com.testProject.productos.dto.EstadoPago;
import com.testProject.productos.dto.FacturaRequest;
import com.testProject.productos.dto.FacturaResponseDTO;
import com.testProject.productos.dto.PagoPendienteDetalladoDTO;
import com.testProject.productos.dto.PagoPendienteResponseDTO;
import com.testProject.productos.model.Factura;
import com.testProject.productos.service.FacturacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    private final FacturacionService facturacionService;

    @Autowired
    public FacturaController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<FacturaResponseDTO>> crearFactura(
            @Valid @RequestBody FacturaRequest request) {
        FacturaResponseDTO factura = facturacionService.crearFactura(request);
        return ResponseEntity.ok(ApiResponseDTO.success(factura));
    }

    @GetMapping("/cliente/{cedula}")
    public ResponseEntity<ApiResponseDTO<List<FacturaResponseDTO>>> obtenerFacturasPorCliente(
            @PathVariable String cedula) {
        List<FacturaResponseDTO> facturas = facturacionService.obtenerFacturasPorCliente(cedula);
        return ResponseEntity.ok(ApiResponseDTO.success(facturas));
    }
    
    @PostMapping("/pago-pendiente")
    public ResponseEntity<ApiResponseDTO<PagoPendienteResponseDTO>> crearPagoPendiente(
            @Valid @RequestBody FacturaRequest request) {
        try {
            PagoPendienteResponseDTO pago = facturacionService.crearPagoPendiente(request);
            return ResponseEntity.ok(ApiResponseDTO.success(pago));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al procesar la solicitud", e);
        }
    }

    @GetMapping("/pago-pendiente/{codigoTransaccion}")
    public ResponseEntity<ApiResponseDTO<PagoPendienteDetalladoDTO>> obtenerDatosPagoPendiente(
            @PathVariable String codigoTransaccion) {
        try {
            if (!codigoTransaccion.startsWith("PAG-")) {
                return ResponseEntity.ok(ApiResponseDTO.error("El código debe comenzar con PAG-"));
            }
            
            PagoPendienteDetalladoDTO datos = facturacionService.obtenerDatosPagoPendiente(codigoTransaccion);
            
            if (datos.getEstado() == EstadoPago.RECHAZADO && datos.getDetalles().isEmpty()) {
                return ResponseEntity.ok(ApiResponseDTO.error("código " + codigoTransaccion + " no existe"));
            }
            
            return ResponseEntity.ok(ApiResponseDTO.success(datos));
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok(ApiResponseDTO.error("Error al procesar los datos del pago"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponseDTO.error("Error inesperado: " + e.getMessage()));
        }
    }

    @PostMapping("/completar-pago/{codigoTransaccion}")
    public ResponseEntity<ApiResponseDTO<FacturaResponseDTO>> completarPago(
            @PathVariable String codigoTransaccion) {
        try {
            FacturaResponseDTO factura = facturacionService.completarPago(codigoTransaccion);
            return ResponseEntity.ok(ApiResponseDTO.success(factura));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al procesar los datos del pago"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Pago pendiente no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.notFound("Pago pendiente"));
            } else if (e.getMessage().equals("El pago ya ha sido procesado")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDTO.error("El pago ya ha sido procesado"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Error al procesar el pago: " + e.getMessage()));
        }
    }
}
