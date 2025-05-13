package com.testProject.productos.dto;

import lombok.Data;

@Data
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    
    public ApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, "Operación exitosa", data);
    }
    
    public static <T> ApiResponseDTO<T> notFound(String entityName) {
        return new ApiResponseDTO<>(false, entityName + " no encontrado", null);
    }
}