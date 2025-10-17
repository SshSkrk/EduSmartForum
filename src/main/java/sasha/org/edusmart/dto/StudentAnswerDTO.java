package sasha.org.edusmart.dto;

import lombok.Data;

@Data
public class StudentAnswerDTO {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer studentSubmissionId;
    private String questionText;
    private String answer;
    private boolean correct;
}
