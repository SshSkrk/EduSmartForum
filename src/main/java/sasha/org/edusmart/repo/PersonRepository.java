package sasha.org.edusmart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.edusmart.model.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    boolean existsByEmail(String email);

    Optional<Person> findByEmail(String email);

    Optional<Person> getByEmail(String email);
}
