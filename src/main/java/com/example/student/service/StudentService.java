package com.example.student.service;

import com.example.student.dto.StudentRequestDTO;
import com.example.student.dto.StudentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    
    Page<StudentResponseDTO> getAllStudents(Pageable pageable);
    
    StudentResponseDTO getStudentById(Long id);
    
    StudentResponseDTO getStudentByEmail(String email);
    
    StudentResponseDTO createStudent(StudentRequestDTO requestDTO);
    
    StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO);
    
    void deleteStudent(Long id);
}
