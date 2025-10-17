package sasha.org.edusmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sasha.org.edusmart.JWT.JwtTokenUtil;
import sasha.org.edusmart.dto.LoginRequestDTO;
import sasha.org.edusmart.dto.PersonDTO;
import sasha.org.edusmart.dto.StudentDTO;
import sasha.org.edusmart.service.PersonService;
import sasha.org.edusmart.service.StudentService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PersonService personService;
    private final StudentService studentService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          PersonService personService,
                          StudentService studentService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.personService = personService;
        this.studentService = studentService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            PersonDTO personDTO = personService.getPersonByEmail(loginRequestDTO.getEmail());
            if (personDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // 🔐 Generate JWT Token
            String token = jwtTokenUtil.generateToken(personDTO.getEmail(), personDTO.getRole());

            // 🧠 Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", token);
            response.put("id", personDTO.getId());
            response.put("email", personDTO.getEmail());
            response.put("verified", personDTO.isVerified());
            response.put("role", personDTO.getRole());
            response.put("firstName", personDTO.getFirstName());
            response.put("lastName", personDTO.getLastName());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully (client should delete the token).");
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        PersonDTO personDTO = personService.getPersonByEmail(authentication.getName());
        if (personDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", personDTO.getId());
        response.put("email", personDTO.getEmail());
        response.put("verified", personDTO.isVerified());
        response.put("role", personDTO.getRole());
        response.put("firstName", personDTO.getFirstName());
        response.put("lastName", personDTO.getLastName());

        return ResponseEntity.ok(response);
    }
}
