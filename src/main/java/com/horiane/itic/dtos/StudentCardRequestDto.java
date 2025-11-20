package com.horiane.itic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCardRequestDto {

    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String program;
    private String year;
    private String photoUrl;
    private String expirationDate; 
}
