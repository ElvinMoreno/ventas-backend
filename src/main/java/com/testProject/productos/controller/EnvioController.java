package com.testProject.productos.controller;

import com.testProject.productos.dto.ApiResponseDTO;
import com.testProject.productos.dto.EnvioDTO;
import com.testProject.productos.service.EnvioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioService envioService;

    @Autowired
    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EnvioDTO>>> obtenerTodosLosEnvios() {
        List<EnvioDTO> envios = envioService.obtenerTodosLosEnvios();
        return ResponseEntity.ok(ApiResponseDTO.success(envios));
    }
}