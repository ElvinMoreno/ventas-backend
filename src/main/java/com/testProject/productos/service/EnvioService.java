package com.testProject.productos.service;

import com.testProject.productos.dto.EnvioDTO;
import com.testProject.productos.model.Envio;
import com.testProject.productos.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    @Autowired
    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    public List<EnvioDTO> obtenerTodosLosEnvios() {
        return envioRepository.findAll().stream()
                .map(envio -> new EnvioDTO(
                        envio.getId(),
                        envio.getUbicacion(),
                        envio.getPrecio()))
                .collect(Collectors.toList());
    }
}