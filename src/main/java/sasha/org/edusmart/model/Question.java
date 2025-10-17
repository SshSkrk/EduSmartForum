package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.edusmart.dto.QuestionDTO;
import sasha.org.edusmart.repo.QuizRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String correctAnswer;

    @ElementCollection
    private List<String> options;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> answers = new ArrayList<>();

    public  Question(String text, String correctAnswer, List<String> options, Quiz quiz) {
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.quiz = quiz;
    }

    public static Question of(QuestionDTO questionDTO, QuizRepository quizRepository) {
        Question question = new Question();
        if (questionDTO.getId() != null) {
            question.setId(questionDTO.getId());
        }
        question.setText(questionDTO.getText());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setOptions(questionDTO.getOptions());

        if (questionDTO.getQuizTitle() != null) {
            Optional<Quiz> quizOptional = quizRepository.findByCourse_Title(questionDTO.getQuizTitle());
            if (quizOptional.isPresent()) {
                question.setQuiz(quizOptional.get());
            }
        }
        return question;
    }

    public QuestionDTO questionDTO() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(this.id);
        questionDTO.setText(this.text);
        questionDTO.setCorrectAnswer(this.correctAnswer);
        questionDTO.setOptions(this.options);
        questionDTO.setQuizTitle(this.quiz.getTitle());

        return questionDTO;
    }
}
