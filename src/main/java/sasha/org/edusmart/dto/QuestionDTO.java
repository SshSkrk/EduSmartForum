package sasha.org.edusmart.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String text;
    private String correctAnswer;
    private List<String> options;
    private String quizTitle;

}
