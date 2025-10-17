package sasha.org.edusmart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.edusmart.model.Student;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> getStudentById(Integer studentId);

    Optional<Student> findByEmail(String email);
}
