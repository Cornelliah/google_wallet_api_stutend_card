package com.horiane.itic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCardResponseDto {

    private String objectId;  
    private String saveUrl;   
    private String message;   
    private boolean success;  
}
