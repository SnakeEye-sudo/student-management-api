package com.example.student.config;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {
        long currentCount = studentRepository.count();
        if (currentCount == 0) {
            log.info("Student database is empty. Seeding 100 student records into PostgreSQL 'student_db'...");

            String[] firstNames = {
                "Aarav", "Vihaan", "Aditya", "Sai", "Arjun", "Reyansh", "Krishna", "Ishaan", "Shaurya", "Atharv",
                "Ananya", "Diya", "Aadhya", "Priya", "Saanvi", "Ishita", "Riya", "Kiara", "Kavya", "Anjali",
                "Rahul", "Amit", "Vikram", "Sneha", "Pooja", "Neha", "Karan", "Siddharth", "Rohan", "Varun",
                "Meera", "Divya", "Swati", "Rakesh", "Sanjay", "Anil", "Sunita", "Anita", "Geeta", "Rajesh",
                "John", "Jane", "Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Henry"
            };

            String[] lastNames = {
                "Sharma", "Patel", "Verma", "Gupta", "Singh", "Kumar", "Joshi", "Mehta", "Reddy", "Nair",
                "Desai", "Rao", "Choudhury", "Das", "Sen", "Bose", "Chatterjee", "Banerjee", "Mukherjee", "Roy",
                "Mishra", "Trivedi", "Pandey", "Dubey", "Shukla", "Agrawal", "Bansal", "Goel", "Garg", "Jalan",
                "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson"
            };

            String[] courses = {
                "Computer Science", "Information Technology", "Electronics", "Mechanical Engineering",
                "Data Science", "Artificial Intelligence", "Cyber Security", "Cloud Computing",
                "Software Engineering", "Civil Engineering", "Electrical Engineering", "Biotechnology"
            };

            List<Student> students = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                String firstName = firstNames[(i - 1) % firstNames.length];
                String lastName = lastNames[(i - 1) % lastNames.length];
                
                // Add index 'i' to the email to guarantee absolute uniqueness across all 100 records
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@gmail.com";
                
                // Generate a valid phone number pattern
                String phone = "+91-" + (9000000000L + (i * 789456L) % 99999999L);
                
                String course = courses[(i - 1) % courses.length];
                int age = 18 + (i % 8); // Ages ranges from 18 to 25

                Student student = Student.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .phone(phone)
                        .course(course)
                        .age(age)
                        .build();

                students.add(student);
            }

            studentRepository.saveAll(students);
            log.info("Successfully seeded 100 student records into the PostgreSQL database!");
        } else {
            log.info("Student table already contains data ({} records). Skipping automatic database seeding.", currentCount);
        }
    }
}
