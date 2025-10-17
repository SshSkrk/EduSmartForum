package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sasha.org.edusmart.dto.CourseDTO;
import sasha.org.edusmart.repo.FileResourceRepository;
import sasha.org.edusmart.repo.ProfessorRepository;
import sasha.org.edusmart.repo.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Student> students =  new ArrayList<>();

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Professor professor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FileResource>  files = new ArrayList<>();

    @Column(length = 2000)
    private String description;

    private List<String> lectionList = new ArrayList<>();

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Quiz quiz;

    public static Course of(CourseDTO courseDTO, StudentRepository studentRepository,
                            ProfessorRepository professorRepository, FileResourceRepository fileResourceRepository) {
        Course course = new Course();
        if (courseDTO.getId() != null) {
            course.setId(courseDTO.getId());
        }
        course.setTitle(courseDTO.getTitle());
        if (courseDTO.getStudentIDs() != null) {
            List<Student> students = courseDTO.getStudentIDs().stream()
                    .map(id -> studentRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            course.setStudents(students);
        }
        if (courseDTO.getProfessorId() != null) {
            Professor professor = professorRepository.findById(courseDTO.getProfessorId()).orElse(null);
            course.setProfessor(professor);
        }
        if (courseDTO.getFileIDs() != null) {
            List<FileResource> fileList = courseDTO.getFileIDs().stream()
                    .map(id -> fileResourceRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            course.setFiles(fileList);
        }
        course.setDescription(courseDTO.getDescription());
        course.setLectionList(courseDTO.getLectionList());
        return course;
    }

    public CourseDTO courseDTO() {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(this.getId());
        courseDTO.setTitle(this.getTitle());
        if (this.getStudents() != null) {
            List<Integer> studentIds = this.getStudents().stream()
                    .map(Student::getId)
                    .collect(Collectors.toList());
            courseDTO.setStudentIDs(studentIds);
        } else {
            courseDTO.setStudentIDs(new ArrayList<>());
        }
        if (this.getProfessor() != null) {
            courseDTO.setProfessorId(this.getProfessor().getId());
        }
        if (this.getFiles() != null) {
            List<Integer> fileIds = this.getFiles().stream()
                    .map(FileResource::getId)
                    .collect(Collectors.toList());
            courseDTO.setFileIDs(fileIds);
        } else {
            courseDTO.setFileIDs(new ArrayList<>());
        }
        courseDTO.setDescription(this.getDescription());
        courseDTO.setLectionList(this.getLectionList());
        return courseDTO;
    }
}
