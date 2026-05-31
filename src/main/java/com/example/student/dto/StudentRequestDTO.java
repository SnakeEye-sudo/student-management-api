package com.example.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    @NotBlank(message = "First name is required and cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required and cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com|hotmail\\.com|icloud\\.com)$", message = "Please use a real email ID (gmail, yahoo, outlook, hotmail, or icloud)")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Phone number must be valid (e.g., +91-9999888877 or 9999888877)")
    private String phone;

    @NotBlank(message = "Course is required")
    @Size(min = 2, max = 50, message = "Course name must be between 2 and 50 characters")
    private String course;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be a positive number")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 100, message = "Age cannot exceed 100")
    private Integer age;
}
