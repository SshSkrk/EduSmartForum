package sasha.org.edusmart.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileResourceDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String fileName;
    private String filePath;
    private String courseTitle;
}
