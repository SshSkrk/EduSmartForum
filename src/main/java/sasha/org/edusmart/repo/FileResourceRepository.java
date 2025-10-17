package sasha.org.edusmart.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sasha.org.edusmart.model.FileResource;

@Repository
public interface FileResourceRepository extends JpaRepository<FileResource,Integer> {

}
