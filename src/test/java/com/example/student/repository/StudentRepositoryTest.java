package com.example.student.repository;

import com.example.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student student;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        student = Student.builder()
                .firstName("Rahul")
                .lastName("Kumar")
                .email("rahul.test@example.com")
                .phone("9876543210")
                .course("Computer Science")
                .age(20)
                .build();
    }

    @Test
    void testSaveAndFindById() {
        Student saved = studentRepository.save(student);
        assertThat(saved.getId()).isNotNull();

        Optional<Student> found = studentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("rahul.test@example.com");
    }

    @Test
    void testFindByEmail() {
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findByEmail("rahul.test@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Rahul");

        Optional<Student> notFound = studentRepository.findByEmail("nonexistent@example.com");
        assertThat(notFound).isEmpty();
    }

    @Test
    void testExistsByEmail() {
        studentRepository.save(student);

        boolean exists = studentRepository.existsByEmail("rahul.test@example.com");
        assertThat(exists).isTrue();

        boolean notExists = studentRepository.existsByEmail("nonexistent@example.com");
        assertThat(notExists).isFalse();
    }

    @Test
    void testExistsByEmailAndIdNot() {
        Student saved1 = studentRepository.save(student);

        Student student2 = Student.builder()
                .firstName("Aman")
                .lastName("Singh")
                .email("aman@example.com")
                .phone("9999999999")
                .course("Mechanical Engineering")
                .age(21)
                .build();
        Student saved2 = studentRepository.save(student2);

        // Checking duplicate email for saved2 when using saved1's email -> Should be true
        boolean duplicateExists = studentRepository.existsByEmailAndIdNot(saved1.getEmail(), saved2.getId());
        assertThat(duplicateExists).isTrue();

        // Checking duplicate email for saved2 when using saved2's own email -> Should be false
        boolean selfEmailCheck = studentRepository.existsByEmailAndIdNot(saved2.getEmail(), saved2.getId());
        assertThat(selfEmailCheck).isFalse();
    }
}
