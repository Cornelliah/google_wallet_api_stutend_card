package com.horiane.itic.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.horiane.itic.dtos.StudentCardRequestDto;
import com.horiane.itic.dtos.StudentCardResponseDto;
import com.horiane.itic.services.StudentCardService;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student-cards")
@RequiredArgsConstructor
public class StudentCardController {

    private final StudentCardService studentCardService;
     
   

    @PostMapping
    public ResponseEntity<StudentCardResponseDto> createCard(@RequestBody StudentCardRequestDto request) 
    {

        StudentCardResponseDto response = studentCardService.createStudentCard(request);
        return ResponseEntity.ok(response);
    }
}
