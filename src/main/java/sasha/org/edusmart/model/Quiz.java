package sasha.org.edusmart.model;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.edusmart.dto.QuizDTO;
import sasha.org.edusmart.repo.CourseRepository;
import sasha.org.edusmart.repo.QuestionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer totalMark = 100;

    @OneToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentQuizSubmission> studentQuizSubmissionList = new ArrayList<>();

    public Quiz(String title, String description, Course course){
        this.title = title;
        this.description = description;
        this.course = course;
    }

    public static Quiz of(QuizDTO quizDTO, CourseRepository courseRepository,
                          QuestionRepository questionRepository) {
        Quiz quiz = new Quiz();
        if (quizDTO.getId() != null) {
            quiz.setId(quizDTO.getId());
        }
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setTotalMark(quizDTO.getTotalMark());
        if(quizDTO.getCourseTitle() != null){
            Optional<Course> courseOptional = courseRepository.findCourseByTitle(quizDTO.getCourseTitle());
            if(courseOptional.isPresent()){
                quiz.setCourse(courseOptional.get());
            }
        }
        List<Question> questions = new ArrayList<>();
        if(quizDTO.getQuestionsText() != null){
            for(String questionText : quizDTO.getQuestionsText()){
                Optional<Question> question = questionRepository.findByText(questionText);
                questions.add(question.get());
            }
            quiz.setQuestions(questions);
        }
        return quiz;
    }

    public QuizDTO quizDTO() {
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(this.id);
        quizDTO.setTitle(this.title);
        quizDTO.setDescription(this.description);
        quizDTO.setTotalMark(this.totalMark);
        if(this.course!=null){
            quizDTO.setCourseTitle(this.course.getTitle());
        }
        if (this.questions!=null){
            quizDTO.setQuestionsText(this.questions.stream().map(Question::getText).collect(Collectors.toList()));
        }
        return quizDTO;
    }
}
