package sasha.org.edusmart.model;

import jakarta.persistence.*;
import lombok.Data;
import sasha.org.edusmart.dto.PersonDTO;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    public Person(){
    }

    public Person(String email, Boolean verified, String password, String firstName, String lastName) {
        this.email = email;
        this.verified = verified;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Person of(PersonDTO personDTO) {
        Person person = new Person();
        if (personDTO.getId() != null) {
            person.setId(personDTO.getId());
        }
        person.setEmail(personDTO.getEmail());
        person.setVerified(personDTO.isVerified());
        person.setPassword(personDTO.getPassword());
        person.setRole(personDTO.getRole());
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        return person;
    }

    public PersonDTO personDTO() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(this.getId());
        personDTO.setEmail(this.getEmail());
        personDTO.setVerified(this.isVerified());
        personDTO.setPassword(this.getPassword());
        personDTO.setRole(this.getRole());
        personDTO.setFirstName(this.getFirstName());
        personDTO.setLastName(this.getLastName());
        return personDTO;
    }
}
