package de.hhu.propra.chicken.aggregates;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DBStudentRepo extends CrudRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByGithubHandle(String githubHandle);

}
