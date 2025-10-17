package sasha.org.edusmart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.edusmart.model.Quiz;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer>{

    Optional<Quiz> findByCourse_Title(String courseTitle);

    Optional<Quiz> findByTitle(String quizTitle);
}
