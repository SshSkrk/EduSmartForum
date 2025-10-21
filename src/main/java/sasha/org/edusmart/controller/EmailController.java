package sasha.org.edusmart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sasha.org.edusmart.dto.PersonDTO;
import sasha.org.edusmart.model.Person;
import sasha.org.edusmart.service.EmailService;
import sasha.org.edusmart.service.PersonService;

import java.util.Date;

@RestController
@RequestMapping("/api")
public class EmailController {
    private final PersonService personService;
    private final EmailService emailService;

    public EmailController(PersonService personService, EmailService emailService) {
        this.personService = personService;
        this.emailService = emailService;
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam String userEmail) {
        try {
            PersonDTO personDTO = personService.getPersonByEmail(userEmail);
            Person person = Person.of(personDTO);

            if (person.isVerified()) {
                return ResponseEntity.badRequest().body("User is already verified.");
            }

            // Build verification link
            //localhost:
            //String link = "http://localhost:8080/api/verify?userEmail=" + person.getEmail();

            String link = "https://edusmart-551269b21410.herokuapp.com/api/verify?userEmail=" + person.getEmail();


            // Send email
            emailService.sendEmail(
                    person.getEmail(),
                    "Verify your email",
                    "Hi " + person.getFirstName() + ",\n\nClick the link below to verify your account:\n" + link
            );

            return ResponseEntity.ok("Verification email sent to " + person.getEmail());

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("❌ " + ex.getMessage());
        }
    }
}

