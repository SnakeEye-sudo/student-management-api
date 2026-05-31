package com.example.student.service.impl;

import com.example.student.dto.StudentRequestDTO;
import com.example.student.dto.StudentResponseDTO;
import com.example.student.exception.StudentNotFoundException;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import com.example.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "students_page", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<StudentResponseDTO> getAllStudents(Pageable pageable) {
        Page<StudentResponseDTO> result = studentRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
        log.info("Operation: RETRIEVE_ALL_STUDENTS | Result Size: {}", result.getNumberOfElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "student", key = "#id")
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        log.info("Operation: RETRIEVE_STUDENT_BY_ID | Student ID: {} | Email: {}", student.getId(), student.getEmail());
        return convertToResponseDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "student_email", key = "#email")
    public StudentResponseDTO getStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with email: " + email));
        log.info("Operation: RETRIEVE_STUDENT_BY_EMAIL | Student ID: {} | Email: {}", student.getId(), student.getEmail());
        return convertToResponseDTO(student);
    }

    @Override
    @CacheEvict(value = {"students_page", "student", "student_email"}, allEntries = true)
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        if (studentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Email address already exists: " + requestDTO.getEmail());
        }

        Student student = Student.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .email(requestDTO.getEmail())
                .phone(requestDTO.getPhone())
                .course(requestDTO.getCourse())
                .age(requestDTO.getAge())
                .build();

        Student savedStudent = studentRepository.save(student);
        log.info("Operation: CREATE_STUDENT | Student ID: {} | Email: {}", savedStudent.getId(), savedStudent.getEmail());
        return convertToResponseDTO(savedStudent);
    }

    @Override
    @CacheEvict(value = {"students_page", "student", "student_email"}, allEntries = true)
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        if (studentRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new IllegalArgumentException("Email address already exists: " + requestDTO.getEmail());
        }

        existingStudent.setFirstName(requestDTO.getFirstName());
        existingStudent.setLastName(requestDTO.getLastName());
        existingStudent.setEmail(requestDTO.getEmail());
        existingStudent.setPhone(requestDTO.getPhone());
        existingStudent.setCourse(requestDTO.getCourse());
        existingStudent.setAge(requestDTO.getAge());

        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Operation: UPDATE_STUDENT | Student ID: {} | Email: {}", updatedStudent.getId(), updatedStudent.getEmail());
        return convertToResponseDTO(updatedStudent);
    }

    @Override
    @CacheEvict(value = {"students_page", "student", "student_email"}, allEntries = true)
    public void deleteStudent(Long id) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        
        String email = existingStudent.getEmail();
        studentRepository.delete(existingStudent);
        log.info("Operation: DELETE_STUDENT | Student ID: {} | Email: {}", id, email);
    }

    private StudentResponseDTO convertToResponseDTO(Student student) {
        return StudentResponseDTO.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .course(student.getCourse())
                .age(student.getAge())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }
}
