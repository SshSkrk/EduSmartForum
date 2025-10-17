package sasha.org.edusmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.edusmart.service.ProfessorService;

@RestController
@RequestMapping("/api")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    //PROFESSOR
    @PostMapping(value = "/professor/assignProfOnCourse/{professorId}/{courseTitle}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> assignProfOnCourse(@PathVariable("professorId") Integer professorId,
                                                @PathVariable("courseTitle") String courseTitle) {
        boolean assigned = professorService.assignProfOnCourse(professorId, courseTitle);
        return assigned
                ? ResponseEntity.status(HttpStatus.CREATED).body("Professor assigned successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to assign professor");
    }
    //PROFESSOR
    @GetMapping(value = "/professor/isAssignedOnCourse/{professorId}/{courseId}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public boolean isAssignedOnCourse( @PathVariable("professorId") Integer professorId,
                                       @PathVariable("courseId") Integer courseId) {
        boolean enrolled = professorService.isAssignedOnCourse(professorId, courseId);
        return enrolled;
    }

}
