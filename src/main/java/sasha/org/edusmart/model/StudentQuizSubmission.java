package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import sasha.org.edusmart.dto.StudentAnswerDTO;
import sasha.org.edusmart.dto.StudentQuizSubmissionDTO;
import sasha.org.edusmart.repo.QuestionRepository;
import sasha.org.edusmart.repo.QuizRepository;
import sasha.org.edusmart.repo.StudentAnswerRepository;
import sasha.org.edusmart.repo.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@RedisHash
public class StudentQuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "studentQuizSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> answers = new ArrayList<>();

    private double score;

    public StudentQuizSubmission(Student student, Quiz quiz, List<StudentAnswer> answers) {
        this.student = student;
        this.quiz = quiz;
        this.answers = answers;
    }

    public static StudentQuizSubmission of(StudentQuizSubmissionDTO dto,
                                           StudentRepository studentRepository,
                                           QuizRepository quizRepository,
                                           StudentAnswerRepository studentAnswerRepository,
                                           QuestionRepository questionRepository) {
        StudentQuizSubmission submission = new StudentQuizSubmission();

        if (dto.getId() != null) {
            submission.setId(dto.getId());
        }

        if (dto.getStudentId() != null) {
            submission.setStudent(studentRepository.findById(dto.getStudentId()).orElse(null));
        }

        if (dto.getQuizTitle() != null) {
            submission.setQuiz(quizRepository.findByCourse_Title(dto.getQuizTitle()).orElse(null));
        }

        // Convert each StudentAnswerDTO in the DTO into a StudentAnswer entity
        List<StudentAnswer> studentAnswers = new ArrayList<>();

        if (dto.getStudentAnswerDTOS() != null) {
            for (StudentAnswerDTO ansDTO : dto.getStudentAnswerDTOS()) {

                // 1️⃣ Fetch existing answer if ID exists, otherwise create new
                StudentAnswer answerEntity = null;
                if (ansDTO.getId() != null) {
                    answerEntity = studentAnswerRepository.findById(ansDTO.getId())
                            .orElse(new StudentAnswer());
                } else {
                    answerEntity = new StudentAnswer();
                }

                // 2️⃣ Set the link to the parent submission
                answerEntity.setStudentQuizSubmission(submission);

                // 3️⃣ Set the actual answer text
                answerEntity.setAnswer(ansDTO.getAnswer());

                // 4️⃣ Set whether the answer is correct
                answerEntity.setCorrect(ansDTO.isCorrect());

                // 5️⃣ Set the question entity if you have a way to fetch it by text
                if (ansDTO.getQuestionText() != null) {
                    Question question = questionRepository.findByText(ansDTO.getQuestionText())
                             .orElseThrow(() -> new RuntimeException("Question not found"));
                     answerEntity.setQuestion(question);
                }

                // 6️⃣ Add the prepared answer to the list
                studentAnswers.add(answerEntity);
            }
        }

        // 7️⃣ Set all answers to the submission
        submission.setAnswers(studentAnswers);
        submission.setScore(dto.getScore());

        return submission;
    }

    public StudentQuizSubmissionDTO studentQuizSubmissionDTO() {
        StudentQuizSubmissionDTO dto = new StudentQuizSubmissionDTO();
        dto.setId(this.getId());
        dto.setStudentId(this.getStudent().getId());
        dto.setQuizTitle(this.getQuiz().getTitle());

        List<StudentAnswerDTO> answerDTOs = this.getAnswers().stream().map(ans -> {
            StudentAnswerDTO aDto = new StudentAnswerDTO();
            aDto.setId(ans.getId());
            aDto.setStudentSubmissionId(this.getId());
            aDto.setQuestionText(ans.getQuestion() != null ? ans.getQuestion().getText() : null);
            aDto.setAnswer(ans.getAnswer());
            aDto.setCorrect(ans.isCorrect());
            return aDto;
        }).collect(Collectors.toList());

        dto.setStudentAnswerDTOS(answerDTOs);
        dto.setScore(this.getScore());

        return dto;
    }
}