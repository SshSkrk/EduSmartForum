package sasha.org.edusmart.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class StudentDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String email;
    private boolean verified;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String courseTitle;
    private Map<Integer, Boolean> lecturesRead;
}