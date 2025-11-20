package com.horiane.itic.services;

import com.horiane.itic.dtos.StudentCardRequestDto;
import com.horiane.itic.dtos.StudentCardResponseDto;

public interface StudentCardService {
	
	 StudentCardResponseDto createStudentCard(StudentCardRequestDto request);
}
