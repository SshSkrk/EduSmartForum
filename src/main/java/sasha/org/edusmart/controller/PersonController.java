package sasha.org.edusmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sasha.org.edusmart.dto.PersonDTO;
import sasha.org.edusmart.service.PersonService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String userEmail) {
        personService.verifyUser(userEmail);
        return ResponseEntity.ok("✅ Email verified successfully!");
    }

    //STUDENT: student
    @PostMapping(value = "/student/createPerson")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createPerson(@RequestBody PersonDTO personDTO) {
        if (personService.existsByEmail(personDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        personDTO.setRole("STUDENT");
        PersonDTO createdPerson = personService.createPerson(personDTO);
        return createdPerson != null
                ? ResponseEntity.status(HttpStatus.CREATED).body("Student registered successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create student");
    }

    //ADMIN: administrator professor student
    @PostMapping(value = "/admin/createPerson")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminCreatePerson(@RequestBody PersonDTO personDTO) {
        if (personService.existsByEmail(personDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        // Validate role value
        List<String> allowedRoles = List.of("ADMIN", "PROFESSOR", "STUDENT");
        if (!allowedRoles.contains(personDTO.getRole().toUpperCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
        }
        PersonDTO createdPerson = personService.createPerson(personDTO);
        return createdPerson != null
                ? ResponseEntity.status(HttpStatus.CREATED).body("User created successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user");
    }


    @PostMapping("/updatePerson")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN') ")
    public ResponseEntity<Boolean> updatePerson(@RequestBody PersonDTO personDTO) {
        PersonDTO updatedPerson = personService.updatePerson(personDTO);

        return updatedPerson != null
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    //ADMIN
    @DeleteMapping("/admin/deletePerson/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deletePerson(@PathVariable("email") String email) {
        boolean deleted = personService.deletePerson(email);
        return deleted
                ? ResponseEntity.ok(true)  // Deleted successfully
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // Peron not found to delete
    }

    //ADMIN
    @GetMapping("/admin/getAllPersonList")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PersonDTO> getAllPersonList() {
        List<PersonDTO> personDTOS = personService.getAllPersonList();
        if (personDTOS.isEmpty()) {
            return new ArrayList<>();
        }
        return personDTOS; // Return all person list

    }
}

