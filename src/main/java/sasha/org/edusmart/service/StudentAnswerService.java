package sasha.org.edusmart.service;

import org.springframework.stereotype.Service;
import sasha.org.edusmart.dto.StudentAnswerDTO;
import sasha.org.edusmart.model.Question;
import sasha.org.edusmart.model.StudentAnswer;
import sasha.org.edusmart.model.StudentQuizSubmission;
import sasha.org.edusmart.repo.QuestionRepository;
import sasha.org.edusmart.repo.StudentAnswerRepository;
import sasha.org.edusmart.repo.StudentQuizSubmissionRepository;


@Service
public class StudentAnswerService {

    private final StudentAnswerRepository studentAnswerRepository;
    private final StudentQuizSubmissionRepository studentQuizSubmissionRepository;
    private final QuestionRepository questionRepository;

    public StudentAnswerService(StudentAnswerRepository studentAnswerRepository,
                                StudentQuizSubmissionRepository studentQuizSubmissionRepository,
                                QuestionRepository questionRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
        this.studentQuizSubmissionRepository = studentQuizSubmissionRepository;
        this.questionRepository = questionRepository;
    }

    public StudentAnswerDTO saveStudentAnswer(StudentAnswerDTO dto) {
        StudentQuizSubmission submission = studentQuizSubmissionRepository
                .findById(dto.getStudentSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        Question question = questionRepository
                .findByText(dto.getQuestionText())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Check correctness
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(dto.getAnswer().trim());

        StudentAnswer answer = new StudentAnswer();
        answer.setStudentQuizSubmission(submission);
        answer.setQuestion(question);
        answer.setAnswer(dto.getAnswer());
        answer.setCorrect(isCorrect); // ✅ save correctness

        StudentAnswer saved = studentAnswerRepository.save(answer);

        // Return DTO
        StudentAnswerDTO response = new StudentAnswerDTO();
        response.setId(saved.getId());
        response.setStudentSubmissionId(saved.getStudentQuizSubmission().getId());
        response.setQuestionText(question.getText());
        response.setAnswer(saved.getAnswer());
        response.setCorrect(saved.isCorrect());
        return response;
    }

}
