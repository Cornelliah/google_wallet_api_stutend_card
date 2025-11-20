package com.horiane.itic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.horiane.itic.models.StudentCard;

public interface StudentCardRepository extends JpaRepository<StudentCard,Long> {

}
