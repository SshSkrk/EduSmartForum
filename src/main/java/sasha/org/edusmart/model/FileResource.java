package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.edusmart.dto.FileResourceDTO;
import sasha.org.edusmart.repo.CourseRepository;

import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
public class FileResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public static FileResource of(FileResourceDTO fileDTO, CourseRepository courseRepository) {
        FileResource file = new FileResource();
        if (fileDTO.getId() != null) {
            file.setId(fileDTO.getId());
        }
        file.setFileName(fileDTO.getFileName());
        file.setFilePath(fileDTO.getFilePath());
        if (fileDTO.getCourseTitle() != null) {
            Optional<Course> courseOptional = courseRepository.findCourseByTitle(fileDTO.getCourseTitle());
            Course course = courseOptional.get();
            file.setCourse(course);
        }
        return file;
    }

    public FileResourceDTO fileDTO() {
        FileResourceDTO fileDTO = new FileResourceDTO();
        fileDTO.setId(this.getId());
        fileDTO.setFileName(this.getFileName());
        fileDTO.setFilePath(this.getFilePath());
        fileDTO.setCourseTitle(this.getCourse().getTitle() != null ? this.getCourse().getTitle() : null);
        return fileDTO;
    }
}
