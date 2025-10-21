package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import sasha.org.edusmart.dto.StudentDTO;
import sasha.org.edusmart.repo.CourseRepository;
import sasha.org.edusmart.repo.StudentQuizSubmissionRepository;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@RedisHash
public class Student extends Person {
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_read_lectures",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @MapKeyColumn(name = "lecture_index")
    @Column(name = "read")
    private Map<Integer, Boolean> lecturesRead = new HashMap<>();

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StudentQuizSubmission  studentQuizSubmission;

    public static Student of(StudentDTO studentDTO, CourseRepository courseRepository,
                             StudentQuizSubmissionRepository studentQuizSubmissionRepository) {
        Student student = new Student();
        if (studentDTO.getId() != null) {
            student.setId(studentDTO.getId());
        }
        student.setEmail(studentDTO.getEmail());
        student.setVerified(studentDTO.isVerified());
        student.setPassword(studentDTO.getPassword());
        student.setRole(studentDTO.getRole());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        if (studentDTO.getCourseTitle() != null) {
            Optional<Course> courseOptional = courseRepository.findCourseByTitle(studentDTO.getCourseTitle());
            Course course = courseOptional.get();
            student.setCourse(course);
        }
        student.setLecturesRead(studentDTO.getLecturesRead());

        return student;
    }

    public StudentDTO studentDTO() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(this.getId());
        studentDTO.setEmail(this.getEmail());
        studentDTO.setVerified(this.isVerified());
        studentDTO.setPassword(this.getPassword());
        studentDTO.setRole(this.getRole());
        studentDTO.setFirstName(this.getFirstName());
        studentDTO.setLastName(this.getLastName());
        studentDTO.setCourseTitle(this.course.getTitle() != null ? this.course.getTitle() : null);
        studentDTO.setLecturesRead(this.lecturesRead);

        return studentDTO;
    }
}