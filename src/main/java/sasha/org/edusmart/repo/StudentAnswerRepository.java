package sasha.org.edusmart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.edusmart.dto.StudentAnswerDTO;
import sasha.org.edusmart.model.StudentAnswer;

import java.util.Arrays;
import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Integer> {

    List<StudentAnswer> findByStudentQuizSubmission_Id(Integer submissionId);

    List<StudentAnswer> findAllByStudentQuizSubmission_Id(Integer id);
}
