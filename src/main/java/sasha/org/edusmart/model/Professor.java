package sasha.org.edusmart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import sasha.org.edusmart.dto.ProfessorDTO;
import sasha.org.edusmart.repo.CourseRepository;

import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
public class Professor extends Person{
    @OneToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public static Professor of(ProfessorDTO professorDTO, CourseRepository courseRepository) {
        Professor professor = new Professor();
        if (professorDTO.getId() != null) {
            professor.setId(professorDTO.getId());
        }
        professor.setEmail(professorDTO.getEmail());
        professor.setVerified(professorDTO.isVerified());
        professor.setPassword(professorDTO.getPassword());
        professor.setRole(professorDTO.getRole());
        professor.setFirstName(professorDTO.getFirstName());
        professor.setLastName(professorDTO.getLastName());
        if (professorDTO.getCourseTitle() != null) {
            Optional<Course> courseOptional = courseRepository.findCourseByTitle(professorDTO.getCourseTitle());
            Course course = courseOptional.get();
            professor.setCourse(course);
        }
        return professor;
    }

    public ProfessorDTO professorDTO() {
        ProfessorDTO professorDTO = new ProfessorDTO();
        professorDTO.setId(this.getId());
        professorDTO.setEmail(this.getEmail());
        professorDTO.setVerified(this.isVerified());
        professorDTO.setPassword(this.getPassword());
        professorDTO.setRole(this.getRole());
        professorDTO.setFirstName(this.getFirstName());
        professorDTO.setLastName(this.getLastName());
        professorDTO.setCourseTitle(this.course.getTitle() != null ? this.course.getTitle() : null);
        return professorDTO;
    }
}
