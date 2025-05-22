package com.testProject.productos.dto;

import com.testProject.productos.model.Cliente;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String direccion;

    public ClienteDTO(Cliente cliente) {
        this.id = cliente.getId();
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.cedula = cliente.getCedula();
        this.direccion = cliente.getDireccion();
    }
}