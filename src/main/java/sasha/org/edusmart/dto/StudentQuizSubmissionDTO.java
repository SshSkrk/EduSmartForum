package sasha.org.edusmart.dto;

import lombok.Data;
import java.util.List;

@Data
public class StudentQuizSubmissionDTO {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer studentId;
    private String quizTitle;
    private List<StudentAnswerDTO> studentAnswerDTOS;
    private double score;
}
