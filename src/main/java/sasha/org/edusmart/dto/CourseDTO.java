package sasha.org.edusmart.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class CourseDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private List<Integer> studentIDs;
    private Integer professorId;
    private List<Integer>  fileIDs;
    private String description;
    private List<String> lectionList;
}
