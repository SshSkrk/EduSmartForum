package sasha.org.edusmart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.edusmart.model.Course;
import sasha.org.edusmart.model.Professor;
import sasha.org.edusmart.model.Student;
import sasha.org.edusmart.repo.CourseRepository;
import sasha.org.edusmart.repo.ProfessorRepository;

import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;

    public ProfessorService(ProfessorRepository professorRepository, CourseRepository courseRepository) {
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
    }

    //Prof
    @Transactional
    public boolean assignProfOnCourse(Integer professorId, String courseTitle) {
        Optional<Professor> professorOptional = professorRepository.getProfessorsById(professorId);
        Optional<Course> courseOptional = courseRepository.findCourseByTitle(courseTitle);
        if (professorOptional.isPresent() && courseOptional.isPresent()) {
            Professor professor = professorOptional.get();
            Course course = courseOptional.get();
            if (professor.getCourse() == null && (course.getProfessor() == null || !course.getProfessor().equals(professor))) {
                professor.setCourse(course);
                course.setProfessor(professor);
                professorRepository.save(professor);
                courseRepository.save(course);
                return true;
            }
        }
        return false;
    }

    //Prof
    @Transactional
    public boolean isAssignedOnCourse(Integer professorId, Integer courseId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        if (professorOptional.isEmpty()) return false;

        Professor professor = professorOptional.get();
        return professor.getCourse().getId().equals(courseId);
    }
}