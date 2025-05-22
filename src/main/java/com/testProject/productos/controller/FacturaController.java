package com.testProject.productos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testProject.productos.dto.ApiResponseDTO;
import com.testProject.productos.dto.FacturaRequest;
import com.testProject.productos.dto.FacturaResponseDTO;
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
}
