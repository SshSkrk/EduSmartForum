package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.edusmart.dto.StudentAnswerDTO;
import sasha.org.edusmart.repo.*;

import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "studentQuizSubmission_id")
    private StudentQuizSubmission studentQuizSubmission;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String answer;

    private boolean correct;

    public StudentAnswer(StudentQuizSubmission studentQuizSubmission, Question question, String answer) {
        this.studentQuizSubmission = studentQuizSubmission;
        this.question = question;
        this.answer = answer;
        this.correct = false;
    }

    public static StudentAnswer of(StudentAnswerDTO studentAnswerDTO,
                                      StudentQuizSubmissionRepository studentQuizSubmissionRepository,
                                      QuestionRepository questionRepository) {
        StudentAnswer studentAnswer = new StudentAnswer();
        if (studentAnswerDTO.getId() != null) {
            studentAnswer.setId(studentAnswerDTO.getId());
        }
        if (studentAnswerDTO.getStudentSubmissionId() != null) {
            Optional<StudentQuizSubmission> studentQuizSubmissionOptional = studentQuizSubmissionRepository
                    .findById(studentAnswerDTO.getStudentSubmissionId());
            if (studentQuizSubmissionOptional.isPresent()) {
                studentAnswer.setStudentQuizSubmission(studentQuizSubmissionOptional.get());
            }
        }
        if (studentAnswerDTO.getQuestionText() != null) {
            Optional<Question> questionOptional= questionRepository.findByText(studentAnswerDTO.getQuestionText());
            if (questionOptional.isPresent()) {
                studentAnswer.setQuestion(questionOptional.get());
            }
        }
        studentAnswer.setAnswer(studentAnswerDTO.getAnswer());
        studentAnswer.setCorrect(studentAnswerDTO.isCorrect());

        return studentAnswer;
    }

    public StudentAnswerDTO studentAnswerDTO() {
        StudentAnswerDTO studentAnswerDTO = new StudentAnswerDTO();
        studentAnswerDTO.setId(this.id);
        studentAnswerDTO.setStudentSubmissionId(this.studentQuizSubmission.getId());
        studentAnswerDTO.setQuestionText(this.question.getText());
        studentAnswerDTO.setAnswer(this.answer);
        studentAnswerDTO.setCorrect(this.correct);

        return studentAnswerDTO;
    }

}
