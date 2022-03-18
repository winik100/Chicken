package de.hhu.propra.chicken.aggregates;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DBStudentRepo extends CrudRepository<StudentEntity, Long> {
    @Query("SELECT * FROM student WHERE github_handle = :handle")
    Optional<StudentEntity> findByGitHubHandle(@Param("handle") String gitHubHandle);
}
