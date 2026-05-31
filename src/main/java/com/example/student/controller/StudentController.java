package com.example.student.controller;

import com.example.student.dto.ApiResponse;
import com.example.student.dto.StudentRequestDTO;
import com.example.student.dto.StudentResponseDTO;
import com.example.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "Endpoints for managing student profiles and records")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all students paginated", description = "Retrieves a paginated, sorted list of all student records in the system.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List retrieved successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT access token")
    public ResponseEntity<ApiResponse<Page<StudentResponseDTO>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy) {

        // Prevent abuse by limiting the max page size
        int MAX_PAGE_SIZE = 100;
        int actualSize = Math.min(size, MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(page, actualSize, Sort.by(sortBy.trim()));
        Page<StudentResponseDTO> students = studentService.getAllStudents(pageable);

        ApiResponse<Page<StudentResponseDTO>> response = ApiResponse.success("Students retrieved successfully",
                students);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieves details of a specific student record by its unique database ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student found successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long id) {
        StudentResponseDTO student = studentService.getStudentById(id);
        ApiResponse<StudentResponseDTO> response = ApiResponse.success("Student retrieved successfully", student);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search student by Email", description = "Searches for a student record by their exact unique email address.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student found successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentByEmail(@RequestParam String email) {
        StudentResponseDTO student = studentService.getStudentByEmail(email);
        ApiResponse<StudentResponseDTO> response = ApiResponse.success("Student found successfully", student);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new student", description = "Registers a new student profile in the system database.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student created successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or duplicate email")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        StudentResponseDTO createdStudent = studentService.createStudent(requestDTO);
        ApiResponse<StudentResponseDTO> response = ApiResponse.success("Student created successfully", createdStudent);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing student", description = "Modifies fields of an existing student record by their ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student updated successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload or email duplicate")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        StudentResponseDTO updatedStudent = studentService.updateStudent(id, requestDTO);
        ApiResponse<StudentResponseDTO> response = ApiResponse.success("Student updated successfully", updatedStudent);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Removes a student record permanently from the database.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        ApiResponse<Void> response = ApiResponse.success("Student deleted successfully");
        return ResponseEntity.ok(response);
    }
}
