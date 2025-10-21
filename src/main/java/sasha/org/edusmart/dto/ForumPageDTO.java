package sasha.org.edusmart.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ForumPageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String theme;
    private List<String> answers;

}
