package sasha.org.edusmart.service;

import org.springframework.stereotype.Service;
import sasha.org.edusmart.dto.QuizDTO;
import sasha.org.edusmart.model.Quiz;
import sasha.org.edusmart.repo.CourseRepository;
import sasha.org.edusmart.repo.QuestionRepository;
import sasha.org.edusmart.repo.QuizRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, CourseRepository courseRepository,
                       QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
    }

    public boolean createQuiz(QuizDTO quizDTO) {
        Optional<Quiz> quizOptional = quizRepository.findByCourse_Title(quizDTO.getTitle());
        if (quizOptional.isEmpty()) {
            quizRepository.save(Quiz.of(quizDTO,courseRepository, questionRepository));
            return true;
        }
        return false;
    }

    public void createQuizWithQuestions(Quiz quiz) {
       quizRepository.save(quiz);

    }

    public List<QuizDTO> findAllQuiz() {
        List<Quiz> quizList = quizRepository.findAll();
        if (quizList.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<QuizDTO> quizDTOS = new ArrayList<>();
            for (Quiz quiz : quizList) {
                QuizDTO quizDTO = new QuizDTO();
                quizDTO.setId(quiz.getId());
                quizDTO.setTitle(quiz.getTitle());
                quizDTO.setDescription(quiz.getDescription());
                quizDTO.setTotalMark(quiz.getTotalMark());
                quizDTO.setCourseTitle(quiz.getCourse().getTitle());
                quizDTO.setQuestionsText(quiz.getQuestions().stream()
                        .map(question -> question.getText()).collect(Collectors.toList()));
                quizDTOS.add(quizDTO);
            }
            return quizDTOS;
        }
    }

    public QuizDTO findQuizByCourseTitle(String courseTitle) {
        Optional<Quiz> quizOptional = quizRepository.findByCourse_Title(courseTitle);
        if (quizOptional.isEmpty()) {
            return null;
        }
        Quiz quiz = quizOptional.get();
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(quiz.getId());
        quizDTO.setTitle(quiz.getTitle());
        quizDTO.setDescription(quiz.getDescription());
        quizDTO.setTotalMark(quiz.getTotalMark());
        quizDTO.setCourseTitle(quiz.getCourse().getTitle());
        quizDTO.setQuestionsText(quiz.getQuestions().stream()
                .map(question -> question.getText()).collect(Collectors.toList()));
        return quizDTO;
    }
}
