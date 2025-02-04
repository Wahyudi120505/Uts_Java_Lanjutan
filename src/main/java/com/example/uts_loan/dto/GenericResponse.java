package com.example.uts_loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GenericResponse<T> {
    private String status; 
    private String message; 
    private T data;        

    public static <T> GenericResponse<T> success(String message, T data) {
        return  GenericResponse.<T>builder()
        .status("Success")
        .message(message)
        .data(data)
        .build();
    }

    public static <T> GenericResponse<T> failed(String message, T data) {
        return GenericResponse.<T>builder()
        .status("Failed")
        .message(message)
        .data(data)
        .build();
    }
    
}
