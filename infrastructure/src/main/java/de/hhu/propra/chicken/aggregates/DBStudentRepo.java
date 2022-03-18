package de.hhu.propra.chicken.aggregates;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface DBStudentRepo extends CrudRepository<StudentEntity, Long> {
    @Query("SELECT id, github_handle, rest_urlaub  FROM chicken.student WHERE github_handle = :handle")
    Optional<StudentEntity> findByGitHubHandle(@Param("handle") String gitHubHandle);

    @Query("SELECT start, ende FROM chicken.urlaubs_eintrag WHERE student_id = :id")
    Set<UrlaubsEintragEntity> findUrlaubByStudentId(@Param("id") Long id);
}
