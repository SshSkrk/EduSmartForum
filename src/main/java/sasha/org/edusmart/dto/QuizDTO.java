package sasha.org.edusmart.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizDTO {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private String description;
    private Integer totalMark;
    private String courseTitle;
    private List<String> questionsText;

}
