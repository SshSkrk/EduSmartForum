package sasha.org.edusmart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.edusmart.model.Quiz;
import sasha.org.edusmart.model.Student;
import sasha.org.edusmart.model.StudentQuizSubmission;

import java.util.Optional;

@Repository
public interface StudentQuizSubmissionRepository extends JpaRepository<StudentQuizSubmission, Integer> {
    Optional<StudentQuizSubmission> findByStudent_Id(Integer studentId);

    boolean existsByStudentAndQuiz(Student student, Quiz quiz);
}