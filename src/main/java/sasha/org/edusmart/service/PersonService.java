package sasha.org.edusmart.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sasha.org.edusmart.dto.PersonDTO;
import sasha.org.edusmart.model.*;
import sasha.org.edusmart.repo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PasswordEncoder encoder;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdministratorRepository administratorRepository;

    public PersonService(PersonRepository personRepository, PasswordEncoder encoder,
                         CourseRepository courseRepository, StudentRepository studentRepository,
                         ProfessorRepository professorRepository, AdministratorRepository administratorRepository) {
        this.personRepository = personRepository;
        this.encoder = encoder;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.administratorRepository = administratorRepository;
    }

    // Cache by person ID
    //@Cacheable(value = "persons", key = "#id")

    // Update cache after modifying DB
    //@CachePut(value = "persons", key = "#person.id")

    // Remove from cache after deleting
    //@CacheEvict(value = "persons", key = "#id")


    //@Cacheable(value = "person", key = "#email", unless = "#result == null")
    @Transactional(readOnly = true)
    public PersonDTO getPersonByEmail(String email) {
        Optional<Person> personOptional = personRepository.findByEmail(email);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            PersonDTO personDTO = person.personDTO();
            return personDTO;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    public void verifyUser(String userEmail) {
        Person person = personRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (person.isVerified()) {
            throw new RuntimeException("User already verified.");
        }

        person.setVerified(true);
        personRepository.save(person);
    }

    //User student, Admin administrator professor student
    @CacheEvict(value = "personsAll", allEntries = true)
    @CachePut(value = "person", key = "#result.id", unless = "#result == null")
    @Transactional
    public PersonDTO createPerson(PersonDTO personDTO) {
        Optional<Person> personOptional = personRepository.getByEmail(personDTO.getEmail());
        if (personOptional.isEmpty()) {
            Person newPerson;

            switch (personDTO.getRole().toUpperCase()) {
                case "STUDENT" -> newPerson = new Student();
                case "PROFESSOR" -> newPerson = new Professor();
                case "ADMIN", "ADMINISTRATOR" -> newPerson = new Administrator();
                default -> throw new IllegalArgumentException("Invalid role: " + personDTO.getRole());
            }

            // Common fields for all Person types
            newPerson.setEmail(personDTO.getEmail());
            newPerson.setFirstName(personDTO.getFirstName());
            newPerson.setLastName(personDTO.getLastName());
            newPerson.setRole(personDTO.getRole().toUpperCase());
            newPerson.setVerified(personDTO.isVerified());
            newPerson.setPassword(encoder.encode(personDTO.getPassword()));

            Person savedPerson = personRepository.save(newPerson);
            return savedPerson.personDTO();
        }

        return null; // already exists
    }


    //User student, Admin administrator professor student
    @CacheEvict(value = "personsAll", allEntries = true)
    @CachePut(value = "person", key = "#result.id", unless = "#result == null")
    @Transactional
    public PersonDTO updatePerson(PersonDTO personDTO) {
        Optional<Person> personOptional = personRepository.getByEmail(personDTO.getEmail());
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            //person.setEmail(personDTO.getEmail());
            //person.setVerified(personDTO.isVerified());
            //person.setRole(personDTO.getRole());
            person.setFirstName(personDTO.getFirstName());
            person.setLastName(personDTO.getLastName());

            // Only encode if it's not already encoded (BCrypt starts with "$2a$")
            String incomingPassword = personDTO.getPassword();
            if (!incomingPassword.startsWith("$2a$")) {
                person.setPassword(encoder.encode(incomingPassword));
            } else {
                person.setPassword(incomingPassword); // already encoded
            }
            Person savedPerson = personRepository.save(person);
            return savedPerson.personDTO();
        }
        return null;
    }

    //Admin

    @CacheEvict(value = {"person", "personsAll"}, allEntries = true)
    @Transactional
    public boolean deletePerson(String email) {
        Optional<Person> personOptional = personRepository.getByEmail(email);
        if (personOptional.isPresent()) {
            Person personToDelete = personOptional.get();

            if (personToDelete instanceof Student student) {
                Course course = student.getCourse();
                if (course != null) {
                    course.getStudents().remove(student);
                    student.setCourse(null);
                    courseRepository.save(course);
                }
                studentRepository.delete(student);
            } else if (personToDelete instanceof Professor professor) {
                Course course = professor.getCourse();
                if (course != null) {
                    course.setProfessor(null);
                    professor.setCourse(null);
                    courseRepository.save(course);
                }
                professorRepository.delete(professor);
            } else if (personToDelete instanceof Administrator administrator) {
                administratorRepository.delete(administrator);
            } else {
                personRepository.delete(personToDelete);
            }
            return true;
        }
        return false;
    }

    //Admin
    @Cacheable(value = "personsAll", unless = "#result == null")
    @Transactional(readOnly = true)
    public List<PersonDTO> getAllPersonList() {
        List<Person> personList = personRepository.findAll();
        if (personList.isEmpty()) {
            return new ArrayList<>();
        }
        List<PersonDTO> personDTOList = new ArrayList<>();
        for (Person person : personList) {
            personDTOList.add(person.personDTO());
        }
        return personDTOList;
    }

}

