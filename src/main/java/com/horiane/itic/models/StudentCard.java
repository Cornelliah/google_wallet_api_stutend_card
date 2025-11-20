package com.horiane.itic.models;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="cards")
@Entity
@Builder
public class StudentCard {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
  private String objectId;
  private String studentId;
  private String firstName;
  private String lastName;
  private String email;
  private String program;
  @Column(name = "academic_year")
  private String year;
  private String photoUrl;
  @Column( length = 3000)
  private String saveUrl; 
  private LocalDateTime expirationDate;
  
}
