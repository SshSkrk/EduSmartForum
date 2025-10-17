package sasha.org.edusmart.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import sasha.org.edusmart.model.Person;
import sasha.org.edusmart.repo.PersonRepository;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    public UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Person> personOptional = personRepository.getByEmail(email);

        if (personOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Person person = personOptional.get();

        return User.builder()
                .username(person.getEmail()) // use email as username
                .password(person.getPassword())
                .authorities("ROLE_" + person.getRole())
                .build();
    }
}

