package sasha.org.edusmart.service;

import org.springframework.stereotype.Service;
import sasha.org.edusmart.dto.QuestionDTO;
import sasha.org.edusmart.model.Question;
import sasha.org.edusmart.repo.QuestionRepository;
import sasha.org.edusmart.repo.QuizRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServise {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionServise(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    public boolean createQuestion(QuestionDTO questionDTO) {
        Optional<Question> questionOptional = questionRepository.findByText(questionDTO.getText());
        if (questionOptional.isEmpty()) {
            questionRepository.save(Question.of(questionDTO, quizRepository));
            return true;
        }
        return false;
    }

    public List<QuestionDTO> findQuestionsByQuizTitle(String quizTitle) {
        List<Question> questions = questionRepository.findAllByQuiz_Title(quizTitle);
        if (questions.isEmpty()) {
            return new ArrayList<>();
        }
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(question.getId());
            questionDTO.setText(question.getText());
            questionDTO.setCorrectAnswer(question.getCorrectAnswer());
            questionDTO.setOptions(question.getOptions());
            questionDTO.setQuizTitle(question.getQuiz().getTitle());
            questionDTOList.add(questionDTO);
        }

        return questionDTOList;
    }
}
